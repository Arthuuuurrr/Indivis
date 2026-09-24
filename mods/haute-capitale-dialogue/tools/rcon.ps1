# Generic RCON runner: reads commands (one per line) from -File, prints "CMD >>> RESPONSE".
param([string]$File, [string]$Password = "hcdtest", [int]$Port = 25575)

function Send-RconPacket($stream, $id, $type, $body) {
    $bodyBytes = [System.Text.Encoding]::UTF8.GetBytes($body)
    $packetLength = 4 + 4 + $bodyBytes.Length + 2
    $ms = New-Object System.IO.MemoryStream
    $writer = New-Object System.IO.BinaryWriter($ms)
    $writer.Write([int32]$packetLength); $writer.Write([int32]$id); $writer.Write([int32]$type)
    $writer.Write($bodyBytes); $writer.Write([byte]0); $writer.Write([byte]0); $writer.Flush()
    $bytes = $ms.ToArray(); $stream.Write($bytes, 0, $bytes.Length); $stream.Flush()
}
function Read-RconPacket($stream) {
    $lenBytes = New-Object byte[] 4
    $stream.Read($lenBytes, 0, 4) | Out-Null
    $length = [System.BitConverter]::ToInt32($lenBytes, 0)
    if ($length -le 0) { return @{ body = "" } }
    $rest = New-Object byte[] $length
    $offset = 0
    while ($offset -lt $length) {
        $read = $stream.Read($rest, $offset, $length - $offset)
        if ($read -le 0) { break }
        $offset += $read
    }
    return @{ body = [System.Text.Encoding]::UTF8.GetString($rest, 8, $length - 10) }
}

$client = New-Object System.Net.Sockets.TcpClient("127.0.0.1", $Port)
$stream = $client.GetStream()
Send-RconPacket $stream 1 3 $Password
Read-RconPacket $stream | Out-Null

$id = 10
foreach ($line in (Get-Content -Path $File -Encoding UTF8)) {
    $cmd = $line.Trim()
    if ($cmd -eq "" -or $cmd.StartsWith("#")) { continue }
    Send-RconPacket $stream $id 2 $cmd
    $resp = (Read-RconPacket $stream).body
    Write-Output "$cmd >>> $resp"
    $id++
}
$client.Close()
