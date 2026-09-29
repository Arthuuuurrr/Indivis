# CubeAnimals rattlesnake inspection

Base bundle: 1.2.45

## Nested JARs
- ./META-INF/jars/animalgarden-hippopotamus.jar
- ./META-INF/jars/animalgarden-westerngorilla.jar
- ./META-INF/jars/animalgarden-whiterhinoceros.jar
- ./META-INF/jars/autonomous-colored-horses.jar
- ./META-INF/jars/autonomous-orc-mobs.jar
- ./META-INF/jars/chaos-mmo-ai.jar
- ./META-INF/jars/cubeanimals.jar
- ./META-INF/jars/deermod.jar
- ./META-INF/jars/dino-mounts-ai.jar
- ./META-INF/jars/hmobs.jar
- ./META-INF/jars/wanderingwildlife.jar

## CubeAnimals JAR
cubeanimals.jar

### fabric.mod.json
{
  "schemaVersion": 1,
  "id": "cubeanimals",
  "version": "1.3-1.21.11",
  "name": "Cube Animals",
  "description": "Cube Animals is a mod that enhances Minecraft experience in the scope of creatures.",
  "authors": [
    "SupRK, xUszaty"
  ],
  "contact": {
    "homepage": "",
    "sources": ""
  },
  "license": "CC0-1.0",
  "icon": "assets/cubeanimals/icon.png",
  "environment": "*",
  "entrypoints": {
    "main": [
      "net.suprk.ufauna.UltraFauna"
    ],
    "fabric-datagen": [
      "net.suprk.ufauna.UltraFaunaDataGenerator"
    ],
    "client": [
      "net.suprk.ufauna.UltraFaunaClient"
    ]
  },
  "mixins": [
    "ufauna.mixins.json",
    {
      "config": "ufauna.client.mixins.json",
      "environment": "client"
    }
  ],
  "depends": {
    "fabricloader": ">=0.18.4",
    "minecraft": "~1.21.11",
    "java": ">=21",
    "fabric-api": "*"
  }
}


### Classes containing snake/rattle
net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderer.class
net/suprk/ufauna/entity/custom/RattleSnakeEntity.class
net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.class
assets/cubeanimals/textures/gui/page_rattlesnake.png
assets/cubeanimals/textures/item/rattlesnake_venom.png
assets/cubeanimals/models/item/rattlesnake_spawn_egg.json
assets/cubeanimals/items/rattlesnake_venom.json
net/suprk/ufauna/entity/client/RattleSnake/
data/cubeanimals/recipe/crafting_rattlesnake_venom.json
assets/cubeanimals/textures/entity/rattlesnake/
net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeModel.class
assets/cubeanimals/sounds/rattlesnake/
assets/cubeanimals/textures/item/rattlesnake_spawn_egg.png
net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeAnimations.class
assets/cubeanimals/textures/entity/rattlesnake/rattlesnake2.png
assets/cubeanimals/models/item/rattlesnake_venom.json
assets/cubeanimals/sounds/rattlesnake/rattle.ogg
assets/cubeanimals/textures/entity/rattlesnake/rattlesnake.png
assets/cubeanimals/items/rattlesnake_spawn_egg.json

## javap

### net.suprk.ufauna.entity.client.RattleSnake.RattleSnakeRenderer
Classfile jar:file:///tmp/cc146/nested/cubeanimals.jar!/net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderer.class
  Last modified Sep 25, 2026; size 3864 bytes
  SHA-256 checksum 7627026606145b988c19c51cd71fcff5f94f762f0638d0f3b332e84842268500
  Compiled from "RattleSnakeRenderer.java"
public class net.suprk.ufauna.entity.client.RattleSnake.RattleSnakeRenderer extends net.minecraft.class_927<net.suprk.ufauna.entity.custom.RattleSnakeEntity, net.suprk.ufauna.entity.client.RattleSnake.RattleSnakeRenderState, net.suprk.ufauna.entity.client.RattleSnake.RattleSnakeModel>
  minor version: 0
  major version: 65
  flags: (0x0021) ACC_PUBLIC, ACC_SUPER
  this_class: #2                          // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderer
  super_class: #5                         // net/minecraft/class_927
  interfaces: 0, fields: 2, methods: 10, attributes: 3
Constant pool:
    #1 = Utf8               net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderer
    #2 = Class              #1            // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderer
    #3 = Utf8               Lnet/minecraft/class_927<Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeModel;>;
    #4 = Utf8               net/minecraft/class_927
    #5 = Class              #4            // net/minecraft/class_927
    #6 = Utf8               RattleSnakeRenderer.java
    #7 = Utf8               net/minecraft/class_5617$class_5618
    #8 = Class              #7            // net/minecraft/class_5617$class_5618
    #9 = Utf8               net/minecraft/class_5617
   #10 = Class              #9            // net/minecraft/class_5617
   #11 = Utf8               class_5618
   #12 = Utf8               TEXTURE
   #13 = Utf8               Lnet/minecraft/class_2960;
   #14 = Utf8               TEXTURE2
   #15 = Utf8               <init>
   #16 = Utf8               (Lnet/minecraft/class_5617$class_5618;)V
   #17 = Utf8               ctx
   #18 = Utf8               net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeModel
   #19 = Class              #18           // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeModel
   #20 = Utf8               LAYER
   #21 = Utf8               Lnet/minecraft/class_5601;
   #22 = NameAndType        #20:#21       // LAYER:Lnet/minecraft/class_5601;
   #23 = Fieldref           #19.#22       // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeModel.LAYER:Lnet/minecraft/class_5601;
   #24 = Utf8               method_32167
   #25 = Utf8               (Lnet/minecraft/class_5601;)Lnet/minecraft/class_630;
   #26 = NameAndType        #24:#25       // method_32167:(Lnet/minecraft/class_5601;)Lnet/minecraft/class_630;
   #27 = Methodref          #8.#26        // net/minecraft/class_5617$class_5618.method_32167:(Lnet/minecraft/class_5601;)Lnet/minecraft/class_630;
   #28 = Utf8               (Lnet/minecraft/class_630;)V
   #29 = NameAndType        #15:#28       // "<init>":(Lnet/minecraft/class_630;)V
   #30 = Methodref          #19.#29       // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeModel."<init>":(Lnet/minecraft/class_630;)V
   #31 = Utf8               (Lnet/minecraft/class_5617$class_5618;Lnet/minecraft/class_583;F)V
   #32 = NameAndType        #15:#31       // "<init>":(Lnet/minecraft/class_5617$class_5618;Lnet/minecraft/class_583;F)V
   #33 = Methodref          #5.#32        // net/minecraft/class_927."<init>":(Lnet/minecraft/class_5617$class_5618;Lnet/minecraft/class_583;F)V
   #34 = Utf8               this
   #35 = Utf8               Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderer;
   #36 = Utf8               Lnet/minecraft/class_5617$class_5618;
   #37 = Utf8               createRenderState
   #38 = Utf8               ()Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;
   #39 = Utf8               net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState
   #40 = Class              #39           // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState
   #41 = Utf8               ()V
   #42 = NameAndType        #15:#41       // "<init>":()V
   #43 = Methodref          #40.#42       // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState."<init>":()V
   #44 = Utf8               updateRenderState
   #45 = Utf8               (Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;F)V
   #46 = Utf8               entity
   #47 = Utf8               state
   #48 = Utf8               tickDelta
   #49 = Utf8               method_62355
   #50 = Utf8               (Lnet/minecraft/class_1309;Lnet/minecraft/class_10042;F)V
   #51 = NameAndType        #49:#50       // method_62355:(Lnet/minecraft/class_1309;Lnet/minecraft/class_10042;F)V
   #52 = Methodref          #5.#51        // net/minecraft/class_927.method_62355:(Lnet/minecraft/class_1309;Lnet/minecraft/class_10042;F)V
   #53 = Utf8               net/suprk/ufauna/entity/custom/RattleSnakeEntity
   #54 = Class              #53           // net/suprk/ufauna/entity/custom/RattleSnakeEntity
   #55 = Utf8               method_6109
   #56 = Utf8               ()Z
   #57 = NameAndType        #55:#56       // method_6109:()Z
   #58 = Methodref          #54.#57       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.method_6109:()Z
   #59 = Utf8               field_53457
   #60 = Utf8               Z
   #61 = NameAndType        #59:#60       // field_53457:Z
   #62 = Fieldref           #40.#61       // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.field_53457:Z
   #63 = Utf8               method_6510
   #64 = NameAndType        #63:#56       // method_6510:()Z
   #65 = Methodref          #54.#64       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.method_6510:()Z
   #66 = Utf8               attacking
   #67 = NameAndType        #66:#60       // attacking:Z
   #68 = Fieldref           #40.#67       // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.attacking:Z
   #69 = Utf8               isCoiledUp
   #70 = NameAndType        #69:#56       // isCoiledUp:()Z
   #71 = Methodref          #54.#70       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.isCoiledUp:()Z
   #72 = Utf8               field_53451
   #73 = Utf8               F
   #74 = NameAndType        #72:#73       // field_53451:F
   #75 = Fieldref           #40.#74       // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.field_53451:F
   #76 = Double             0.1d
   #78 = Utf8               coiledUp
   #79 = NameAndType        #78:#60       // coiledUp:Z
   #80 = Fieldref           #40.#79       // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.coiledUp:Z
   #81 = Utf8               getCoiledTicks
   #82 = Utf8               ()I
   #83 = NameAndType        #81:#82       // getCoiledTicks:()I
   #84 = Methodref          #54.#83       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.getCoiledTicks:()I
   #85 = Utf8               coiledTicks
   #86 = Utf8               I
   #87 = NameAndType        #85:#86       // coiledTicks:I
   #88 = Fieldref           #40.#87       // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.coiledTicks:I
   #89 = Utf8               partialTicks
   #90 = NameAndType        #89:#73       // partialTicks:F
   #91 = Fieldref           #40.#90       // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.partialTicks:F
   #92 = Utf8               getAttackAnimTicks
   #93 = NameAndType        #92:#82       // getAttackAnimTicks:()I
   #94 = Methodref          #54.#93       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.getAttackAnimTicks:()I
   #95 = Utf8               attackTicks
   #96 = NameAndType        #95:#86       // attackTicks:I
   #97 = Fieldref           #40.#96       // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.attackTicks:I
   #98 = Utf8               getVariant
   #99 = NameAndType        #98:#82       // getVariant:()I
  #100 = Methodref          #54.#99       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.getVariant:()I
  #101 = Utf8               variant
  #102 = NameAndType        #101:#86      // variant:I
  #103 = Fieldref           #40.#102      // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.variant:I
  #104 = Utf8               Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;
  #105 = Utf8               Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;
  #106 = Utf8               getTexture
  #107 = Utf8               (Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;)Lnet/minecraft/class_2960;
  #108 = NameAndType        #14:#13       // TEXTURE2:Lnet/minecraft/class_2960;
  #109 = Fieldref           #2.#108       // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderer.TEXTURE2:Lnet/minecraft/class_2960;
  #110 = NameAndType        #12:#13       // TEXTURE:Lnet/minecraft/class_2960;
  #111 = Fieldref           #2.#110       // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderer.TEXTURE:Lnet/minecraft/class_2960;
  #112 = NameAndType        #44:#45       // updateRenderState:(Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;F)V
  #113 = Methodref          #2.#112       // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderer.updateRenderState:(Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;F)V
  #114 = Utf8               method_3885
  #115 = Utf8               (Lnet/minecraft/class_10042;)Lnet/minecraft/class_2960;
  #116 = NameAndType        #106:#107     // getTexture:(Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;)Lnet/minecraft/class_2960;
  #117 = Methodref          #2.#116       // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderer.getTexture:(Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;)Lnet/minecraft/class_2960;
  #118 = Utf8               method_62354
  #119 = Utf8               (Lnet/minecraft/class_1297;Lnet/minecraft/class_10017;F)V
  #120 = Utf8               method_55269
  #121 = Utf8               ()Lnet/minecraft/class_10017;
  #122 = NameAndType        #37:#38       // createRenderState:()Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;
  #123 = Methodref          #2.#122       // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderer.createRenderState:()Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;
  #124 = Utf8               method_55831
  #125 = Utf8               (Lnet/minecraft/class_10017;)F
  #126 = Utf8               net/minecraft/class_10042
  #127 = Class              #126          // net/minecraft/class_10042
  #128 = Utf8               method_55832
  #129 = Utf8               (Lnet/minecraft/class_10042;)F
  #130 = NameAndType        #128:#129     // method_55832:(Lnet/minecraft/class_10042;)F
  #131 = Methodref          #5.#130       // net/minecraft/class_927.method_55832:(Lnet/minecraft/class_10042;)F
  #132 = Utf8               <clinit>
  #133 = Utf8               cubeanimals
  #134 = String             #133          // cubeanimals
  #135 = Utf8               textures/entity/rattlesnake/rattlesnake.png
  #136 = String             #135          // textures/entity/rattlesnake/rattlesnake.png
  #137 = Utf8               net/minecraft/class_2960
  #138 = Class              #137          // net/minecraft/class_2960
  #139 = Utf8               method_60655
  #140 = Utf8               (Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/class_2960;
  #141 = NameAndType        #139:#140     // method_60655:(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/class_2960;
  #142 = Methodref          #138.#141     // net/minecraft/class_2960.method_60655:(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/class_2960;
  #143 = Utf8               textures/entity/rattlesnake/rattlesnake2.png
  #144 = String             #143          // textures/entity/rattlesnake/rattlesnake2.png
  #145 = Utf8               Code
  #146 = Utf8               LineNumberTable
  #147 = Utf8               LocalVariableTable
  #148 = Utf8               MethodParameters
  #149 = Utf8               StackMapTable
  #150 = Utf8               InnerClasses
  #151 = Utf8               Signature
  #152 = Utf8               SourceFile
{
  private static final net.minecraft.class_2960 TEXTURE;
    descriptor: Lnet/minecraft/class_2960;
    flags: (0x001a) ACC_PRIVATE, ACC_STATIC, ACC_FINAL

  private static final net.minecraft.class_2960 TEXTURE2;
    descriptor: Lnet/minecraft/class_2960;
    flags: (0x001a) ACC_PRIVATE, ACC_STATIC, ACC_FINAL

  public net.suprk.ufauna.entity.client.RattleSnake.RattleSnakeRenderer(net.minecraft.class_5617$class_5618);
    descriptor: (Lnet/minecraft/class_5617$class_5618;)V
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=6, locals=2, args_size=2
         0: aload_0
         1: aload_1
         2: new           #19                 // class net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeModel
         5: dup
         6: aload_1
         7: getstatic     #23                 // Field net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeModel.LAYER:Lnet/minecraft/class_5601;
        10: invokevirtual #27                 // Method net/minecraft/class_5617$class_5618.method_32167:(Lnet/minecraft/class_5601;)Lnet/minecraft/class_630;
        13: invokespecial #30                 // Method net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeModel."<init>":(Lnet/minecraft/class_630;)V
        16: fconst_0
        17: invokespecial #33                 // Method net/minecraft/class_927."<init>":(Lnet/minecraft/class_5617$class_5618;Lnet/minecraft/class_583;F)V
        20: return
      LineNumberTable:
        line 17: 0
        line 18: 20
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      21     0  this   Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderer;
            0      21     1   ctx   Lnet/minecraft/class_5617$class_5618;
    MethodParameters:
      Name                           Flags
      ctx

  public net.suprk.ufauna.entity.client.RattleSnake.RattleSnakeRenderState createRenderState();
    descriptor: ()Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=2, locals=1, args_size=1
         0: new           #40                 // class net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState
         3: dup
         4: invokespecial #43                 // Method net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState."<init>":()V
         7: areturn
      LineNumberTable:
        line 22: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       8     0  this   Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderer;

  public void updateRenderState(net.suprk.ufauna.entity.custom.RattleSnakeEntity, net.suprk.ufauna.entity.client.RattleSnake.RattleSnakeRenderState, float);
    descriptor: (Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;F)V
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=5, locals=4, args_size=4
         0: aload_0
         1: aload_1
         2: aload_2
         3: fload_3
         4: invokespecial #52                 // Method net/minecraft/class_927.method_62355:(Lnet/minecraft/class_1309;Lnet/minecraft/class_10042;F)V
         7: aload_2
         8: aload_1
         9: invokevirtual #58                 // Method net/suprk/ufauna/entity/custom/RattleSnakeEntity.method_6109:()Z
        12: putfield      #62                 // Field net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.field_53457:Z
        15: aload_2
        16: aload_1
        17: invokevirtual #65                 // Method net/suprk/ufauna/entity/custom/RattleSnakeEntity.method_6510:()Z
        20: putfield      #68                 // Field net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.attacking:Z
        23: aload_2
        24: aload_1
        25: invokevirtual #71                 // Method net/suprk/ufauna/entity/custom/RattleSnakeEntity.isCoiledUp:()Z
        28: ifeq          54
        31: aload_2
        32: getfield      #68                 // Field net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.attacking:Z
        35: ifne          54
        38: aload_2
        39: getfield      #75                 // Field net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.field_53451:F
        42: f2d
        43: ldc2_w        #76                 // double 0.1d
        46: dcmpg
        47: ifge          54
        50: iconst_1
        51: goto          55
        54: iconst_0
        55: putfield      #80                 // Field net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.coiledUp:Z
        58: aload_2
        59: aload_1
        60: invokevirtual #84                 // Method net/suprk/ufauna/entity/custom/RattleSnakeEntity.getCoiledTicks:()I
        63: putfield      #88                 // Field net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.coiledTicks:I
        66: aload_2
        67: fload_3
        68: putfield      #91                 // Field net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.partialTicks:F
        71: aload_2
        72: aload_1
        73: invokevirtual #94                 // Method net/suprk/ufauna/entity/custom/RattleSnakeEntity.getAttackAnimTicks:()I
        76: putfield      #97                 // Field net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.attackTicks:I
        79: aload_2
        80: aload_1
        81: invokevirtual #100                // Method net/suprk/ufauna/entity/custom/RattleSnakeEntity.getVariant:()I
        84: putfield      #103                // Field net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.variant:I
        87: return
      StackMapTable: number_of_entries = 2
        frame_type = 118 /* same_locals_1_stack_item */
          stack = [ class net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState ]
        frame_type = 255 /* full_frame */
          offset_delta = 0
          locals = [ class net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderer, class net/suprk/ufauna/entity/custom/RattleSnakeEntity, class net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState, float ]
          stack = [ class net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState, int ]
      LineNumberTable:
        line 31: 0
        line 32: 7
        line 33: 15
        line 34: 23
        line 35: 58
        line 36: 66
        line 37: 71
        line 38: 79
        line 39: 87
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      88     0  this   Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderer;
            0      88     1 entity   Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;
            0      88     2 state   Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;
            0      88     3 tickDelta   F
    MethodParameters:
      Name                           Flags
      entity
      state
      tickDelta

  public net.minecraft.class_2960 getTexture(net.suprk.ufauna.entity.client.RattleSnake.RattleSnakeRenderState);
    descriptor: (Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;)Lnet/minecraft/class_2960;
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=2, locals=2, args_size=2
         0: aload_1
         1: getfield      #103                // Field net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.variant:I
         4: iconst_1
         5: if_icmpne     12
         8: getstatic     #109                // Field TEXTURE2:Lnet/minecraft/class_2960;
        11: areturn
        12: getstatic     #111                // Field TEXTURE:Lnet/minecraft/class_2960;
        15: areturn
      StackMapTable: number_of_entries = 1
        frame_type = 12 /* same */
      LineNumberTable:
        line 43: 0
        line 44: 12
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      16     0  this   Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderer;
            0      16     1 state   Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;
    MethodParameters:
      Name                           Flags
      state

  public void method_62355(net.minecraft.class_1309, net.minecraft.class_10042, float);
    descriptor: (Lnet/minecraft/class_1309;Lnet/minecraft/class_10042;F)V
    flags: (0x1041) ACC_PUBLIC, ACC_BRIDGE, ACC_SYNTHETIC
    Code:
      stack=4, locals=4, args_size=4
         0: aload_0
         1: aload_1
         2: checkcast     #54                 // class net/suprk/ufauna/entity/custom/RattleSnakeEntity
         5: aload_2
         6: checkcast     #40                 // class net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState
         9: fload_3
        10: invokevirtual #113                // Method updateRenderState:(Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;F)V
        13: return
      LineNumberTable:
        line 9: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      14     0  this   Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderer;
    MethodParameters:
      Name                           Flags
      <no name>                      synthetic
      <no name>                      synthetic
      <no name>                      synthetic

  public net.minecraft.class_2960 method_3885(net.minecraft.class_10042);
    descriptor: (Lnet/minecraft/class_10042;)Lnet/minecraft/class_2960;
    flags: (0x1041) ACC_PUBLIC, ACC_BRIDGE, ACC_SYNTHETIC
    Code:
      stack=2, locals=2, args_size=2
         0: aload_0
         1: aload_1
         2: checkcast     #40                 // class net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState
         5: invokevirtual #117                // Method getTexture:(Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;)Lnet/minecraft/class_2960;
         8: areturn
      LineNumberTable:
        line 9: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       9     0  this   Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderer;
    MethodParameters:
      Name                           Flags
      <no name>                      synthetic

  public void method_62354(net.minecraft.class_1297, net.minecraft.class_10017, float);
    descriptor: (Lnet/minecraft/class_1297;Lnet/minecraft/class_10017;F)V
    flags: (0x1041) ACC_PUBLIC, ACC_BRIDGE, ACC_SYNTHETIC
    Code:
      stack=4, locals=4, args_size=4
         0: aload_0
         1: aload_1
         2: checkcast     #54                 // class net/suprk/ufauna/entity/custom/RattleSnakeEntity
         5: aload_2
         6: checkcast     #40                 // class net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState
         9: fload_3
        10: invokevirtual #113                // Method updateRenderState:(Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;F)V
        13: return
      LineNumberTable:
        line 9: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      14     0  this   Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderer;
    MethodParameters:
      Name                           Flags
      <no name>                      synthetic
      <no name>                      synthetic
      <no name>                      synthetic

  public net.minecraft.class_10017 method_55269();
    descriptor: ()Lnet/minecraft/class_10017;
    flags: (0x1041) ACC_PUBLIC, ACC_BRIDGE, ACC_SYNTHETIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokevirtual #123                // Method createRenderState:()Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;
         4: areturn
      LineNumberTable:
        line 9: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       5     0  this   Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderer;

  protected float method_55831(net.minecraft.class_10017);
    descriptor: (Lnet/minecraft/class_10017;)F
    flags: (0x1044) ACC_PROTECTED, ACC_BRIDGE, ACC_SYNTHETIC
    Code:
      stack=2, locals=2, args_size=2
         0: aload_0
         1: aload_1
         2: checkcast     #127                // class net/minecraft/class_10042
         5: invokespecial #131                // Method net/minecraft/class_927.method_55832:(Lnet/minecraft/class_10042;)F
         8: freturn
      LineNumberTable:
        line 9: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       9     0  this   Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderer;
    MethodParameters:
      Name                           Flags
      <no name>                      synthetic

  static {};
    descriptor: ()V
    flags: (0x0008) ACC_STATIC
    Code:
      stack=2, locals=0, args_size=0
         0: ldc           #134                // String cubeanimals
         2: ldc           #136                // String textures/entity/rattlesnake/rattlesnake.png
         4: invokestatic  #142                // Method net/minecraft/class_2960.method_60655:(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/class_2960;
         7: putstatic     #111                // Field TEXTURE:Lnet/minecraft/class_2960;
        10: ldc           #134                // String cubeanimals
        12: ldc           #144                // String textures/entity/rattlesnake/rattlesnake2.png
        14: invokestatic  #142                // Method net/minecraft/class_2960.method_60655:(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/class_2960;
        17: putstatic     #109                // Field TEXTURE2:Lnet/minecraft/class_2960;
        20: return
      LineNumberTable:
        line 11: 0
        line 12: 4
        line 13: 10
        line 14: 14
        line 13: 20
}
InnerClasses:
  public static #11= #8 of #10;           // class_5618=class net/minecraft/class_5617$class_5618 of class net/minecraft/class_5617
Signature: #3                           // Lnet/minecraft/class_927<Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeModel;>;
SourceFile: "RattleSnakeRenderer.java"

### net.suprk.ufauna.entity.custom.RattleSnakeEntity
Classfile jar:file:///tmp/cc146/nested/cubeanimals.jar!/net/suprk/ufauna/entity/custom/RattleSnakeEntity.class
  Last modified Sep 25, 2026; size 15377 bytes
  SHA-256 checksum 465e3dc1d24d93ffadc3d08a3da029b358341d7e3c0336bc2fa2f360b723bb06
  Compiled from "RattleSnakeEntity.java"
public class net.suprk.ufauna.entity.custom.RattleSnakeEntity extends net.minecraft.class_1429 implements net.suprk.ufauna.entity.custom.NeutralAnimal
  minor version: 0
  major version: 65
  flags: (0x0021) ACC_PUBLIC, ACC_SUPER
  this_class: #2                          // net/suprk/ufauna/entity/custom/RattleSnakeEntity
  super_class: #4                         // net/minecraft/class_1429
  interfaces: 1, fields: 11, methods: 30, attributes: 2
Constant pool:
    #1 = Utf8               net/suprk/ufauna/entity/custom/RattleSnakeEntity
    #2 = Class              #1            // net/suprk/ufauna/entity/custom/RattleSnakeEntity
    #3 = Utf8               net/minecraft/class_1429
    #4 = Class              #3            // net/minecraft/class_1429
    #5 = Utf8               net/suprk/ufauna/entity/custom/NeutralAnimal
    #6 = Class              #5            // net/suprk/ufauna/entity/custom/NeutralAnimal
    #7 = Utf8               RattleSnakeEntity.java
    #8 = Utf8               net/minecraft/class_5132$class_5133
    #9 = Class              #8            // net/minecraft/class_5132$class_5133
   #10 = Utf8               net/minecraft/class_5132
   #11 = Class              #10           // net/minecraft/class_5132
   #12 = Utf8               class_5133
   #13 = Utf8               net/minecraft/class_2945$class_9222
   #14 = Class              #13           // net/minecraft/class_2945$class_9222
   #15 = Utf8               net/minecraft/class_2945
   #16 = Class              #15           // net/minecraft/class_2945
   #17 = Utf8               class_9222
   #18 = Utf8               net/minecraft/class_1269$class_9860
   #19 = Class              #18           // net/minecraft/class_1269$class_9860
   #20 = Utf8               net/minecraft/class_1269
   #21 = Class              #20           // net/minecraft/class_1269
   #22 = Utf8               class_9860
   #23 = Utf8               idleAnimationState
   #24 = Utf8               Lnet/minecraft/class_7094;
   #25 = Utf8               idleAnimationTimeout
   #26 = Utf8               I
   #27 = Utf8               COILED_UP
   #28 = Utf8               Lnet/minecraft/class_2940;
   #29 = Utf8               Lnet/minecraft/class_2940<Ljava/lang/Boolean;>;
   #30 = Utf8               ATTACKING
   #31 = Utf8               VARIANT
   #32 = Utf8               Lnet/minecraft/class_2940<Ljava/lang/Integer;>;
   #33 = Utf8               attackCooldown
   #34 = Utf8               rattleCooldown
   #35 = Utf8               coiledTicks
   #36 = Utf8               attackTicks
   #37 = Utf8               attackAnimTicks
   #38 = Utf8               ATTACK_ANIM_DURATION
   #39 = Integer            8
   #40 = Utf8               <init>
   #41 = Utf8               (Lnet/minecraft/class_1299;Lnet/minecraft/class_1937;)V
   #42 = Utf8               (Lnet/minecraft/class_1299<+Lnet/minecraft/class_1429;>;Lnet/minecraft/class_1937;)V
   #43 = Utf8               entityType
   #44 = Utf8               world
   #45 = NameAndType        #40:#41       // "<init>":(Lnet/minecraft/class_1299;Lnet/minecraft/class_1937;)V
   #46 = Methodref          #4.#45        // net/minecraft/class_1429."<init>":(Lnet/minecraft/class_1299;Lnet/minecraft/class_1937;)V
   #47 = Utf8               net/minecraft/class_7094
   #48 = Class              #47           // net/minecraft/class_7094
   #49 = Utf8               ()V
   #50 = NameAndType        #40:#49       // "<init>":()V
   #51 = Methodref          #48.#50       // net/minecraft/class_7094."<init>":()V
   #52 = NameAndType        #23:#24       // idleAnimationState:Lnet/minecraft/class_7094;
   #53 = Fieldref           #2.#52        // net/suprk/ufauna/entity/custom/RattleSnakeEntity.idleAnimationState:Lnet/minecraft/class_7094;
   #54 = NameAndType        #25:#26       // idleAnimationTimeout:I
   #55 = Fieldref           #2.#54        // net/suprk/ufauna/entity/custom/RattleSnakeEntity.idleAnimationTimeout:I
   #56 = NameAndType        #33:#26       // attackCooldown:I
   #57 = Fieldref           #2.#56        // net/suprk/ufauna/entity/custom/RattleSnakeEntity.attackCooldown:I
   #58 = NameAndType        #34:#26       // rattleCooldown:I
   #59 = Fieldref           #2.#58        // net/suprk/ufauna/entity/custom/RattleSnakeEntity.rattleCooldown:I
   #60 = NameAndType        #37:#26       // attackAnimTicks:I
   #61 = Fieldref           #2.#60        // net/suprk/ufauna/entity/custom/RattleSnakeEntity.attackAnimTicks:I
   #62 = Utf8               this
   #63 = Utf8               Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;
   #64 = Utf8               Lnet/minecraft/class_1299<+Lnet/minecraft/class_1429;>;
   #65 = Utf8               Lnet/minecraft/class_1299;
   #66 = Utf8               Lnet/minecraft/class_1937;
   #67 = Utf8               method_5959
   #68 = Utf8               field_6201
   #69 = Utf8               Lnet/minecraft/class_1355;
   #70 = NameAndType        #68:#69       // field_6201:Lnet/minecraft/class_1355;
   #71 = Fieldref           #2.#70        // net/suprk/ufauna/entity/custom/RattleSnakeEntity.field_6201:Lnet/minecraft/class_1355;
   #72 = Utf8               net/minecraft/class_1347
   #73 = Class              #72           // net/minecraft/class_1347
   #74 = Utf8               (Lnet/minecraft/class_1308;)V
   #75 = NameAndType        #40:#74       // "<init>":(Lnet/minecraft/class_1308;)V
   #76 = Methodref          #73.#75       // net/minecraft/class_1347."<init>":(Lnet/minecraft/class_1308;)V
   #77 = Utf8               net/minecraft/class_1355
   #78 = Class              #77           // net/minecraft/class_1355
   #79 = Utf8               method_6277
   #80 = Utf8               (ILnet/minecraft/class_1352;)V
   #81 = NameAndType        #79:#80       // method_6277:(ILnet/minecraft/class_1352;)V
   #82 = Methodref          #78.#81       // net/minecraft/class_1355.method_6277:(ILnet/minecraft/class_1352;)V
   #83 = Utf8               net/minecraft/class_1341
   #84 = Class              #83           // net/minecraft/class_1341
   #85 = Double             1.15d
   #87 = Utf8               (Lnet/minecraft/class_1429;D)V
   #88 = NameAndType        #40:#87       // "<init>":(Lnet/minecraft/class_1429;D)V
   #89 = Methodref          #84.#88       // net/minecraft/class_1341."<init>":(Lnet/minecraft/class_1429;D)V
   #90 = Utf8               net/minecraft/class_1391
   #91 = Class              #90           // net/minecraft/class_1391
   #92 = Utf8               net/minecraft/class_1935
   #93 = Class              #92           // net/minecraft/class_1935
   #94 = Utf8               net/minecraft/class_1802
   #95 = Class              #94           // net/minecraft/class_1802
   #96 = Utf8               field_8504
   #97 = Utf8               Lnet/minecraft/class_1792;
   #98 = NameAndType        #96:#97       // field_8504:Lnet/minecraft/class_1792;
   #99 = Fieldref           #95.#98       // net/minecraft/class_1802.field_8504:Lnet/minecraft/class_1792;
  #100 = Utf8               net/minecraft/class_1856
  #101 = Class              #100          // net/minecraft/class_1856
  #102 = Utf8               method_8091
  #103 = Utf8               ([Lnet/minecraft/class_1935;)Lnet/minecraft/class_1856;
  #104 = NameAndType        #102:#103     // method_8091:([Lnet/minecraft/class_1935;)Lnet/minecraft/class_1856;
  #105 = Methodref          #101.#104     // net/minecraft/class_1856.method_8091:([Lnet/minecraft/class_1935;)Lnet/minecraft/class_1856;
  #106 = Utf8               (Lnet/minecraft/class_1314;DLjava/util/function/Predicate;Z)V
  #107 = NameAndType        #40:#106      // "<init>":(Lnet/minecraft/class_1314;DLjava/util/function/Predicate;Z)V
  #108 = Methodref          #91.#107      // net/minecraft/class_1391."<init>":(Lnet/minecraft/class_1314;DLjava/util/function/Predicate;Z)V
  #109 = Utf8               net/suprk/ufauna/entity/ai/CoilUpGoal
  #110 = Class              #109          // net/suprk/ufauna/entity/ai/CoilUpGoal
  #111 = Utf8               (Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;)V
  #112 = NameAndType        #40:#111      // "<init>":(Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;)V
  #113 = Methodref          #110.#112     // net/suprk/ufauna/entity/ai/CoilUpGoal."<init>":(Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;)V
  #114 = Utf8               net/minecraft/class_1361
  #115 = Class              #114          // net/minecraft/class_1361
  #116 = Utf8               net/minecraft/class_1657
  #117 = Class              #116          // net/minecraft/class_1657
  #118 = Float              15.0f
  #119 = Utf8               (Lnet/minecraft/class_1308;Ljava/lang/Class;F)V
  #120 = NameAndType        #40:#119      // "<init>":(Lnet/minecraft/class_1308;Ljava/lang/Class;F)V
  #121 = Methodref          #115.#120     // net/minecraft/class_1361."<init>":(Lnet/minecraft/class_1308;Ljava/lang/Class;F)V
  #122 = Utf8               net/minecraft/class_1379
  #123 = Class              #122          // net/minecraft/class_1379
  #124 = Utf8               (Lnet/minecraft/class_1314;D)V
  #125 = NameAndType        #40:#124      // "<init>":(Lnet/minecraft/class_1314;D)V
  #126 = Methodref          #123.#125     // net/minecraft/class_1379."<init>":(Lnet/minecraft/class_1314;D)V
  #127 = Utf8               net/minecraft/class_1376
  #128 = Class              #127          // net/minecraft/class_1376
  #129 = Methodref          #128.#75      // net/minecraft/class_1376."<init>":(Lnet/minecraft/class_1308;)V
  #130 = Utf8               field_6185
  #131 = NameAndType        #130:#69      // field_6185:Lnet/minecraft/class_1355;
  #132 = Fieldref           #2.#131       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.field_6185:Lnet/minecraft/class_1355;
  #133 = Utf8               net/minecraft/class_1400
  #134 = Class              #133          // net/minecraft/class_1400
  #135 = Utf8               (Lnet/minecraft/class_1308;Ljava/lang/Class;Z)V
  #136 = NameAndType        #40:#135      // "<init>":(Lnet/minecraft/class_1308;Ljava/lang/Class;Z)V
  #137 = Methodref          #134.#136     // net/minecraft/class_1400."<init>":(Lnet/minecraft/class_1308;Ljava/lang/Class;Z)V
  #138 = Utf8               net/minecraft/class_1463
  #139 = Class              #138          // net/minecraft/class_1463
  #140 = Utf8               net/suprk/ufauna/entity/custom/FennecEntity
  #141 = Class              #140          // net/suprk/ufauna/entity/custom/FennecEntity
  #142 = Utf8               net/minecraft/class_9069
  #143 = Class              #142          // net/minecraft/class_9069
  #144 = Utf8               createAttributes
  #145 = Utf8               ()Lnet/minecraft/class_5132$class_5133;
  #146 = Utf8               net/minecraft/class_1308
  #147 = Class              #146          // net/minecraft/class_1308
  #148 = Utf8               method_26828
  #149 = NameAndType        #148:#145     // method_26828:()Lnet/minecraft/class_5132$class_5133;
  #150 = Methodref          #147.#149     // net/minecraft/class_1308.method_26828:()Lnet/minecraft/class_5132$class_5133;
  #151 = Utf8               net/minecraft/class_5134
  #152 = Class              #151          // net/minecraft/class_5134
  #153 = Utf8               field_23716
  #154 = Utf8               Lnet/minecraft/class_6880;
  #155 = NameAndType        #153:#154     // field_23716:Lnet/minecraft/class_6880;
  #156 = Fieldref           #152.#155     // net/minecraft/class_5134.field_23716:Lnet/minecraft/class_6880;
  #157 = Double             12.0d
  #159 = Utf8               method_26868
  #160 = Utf8               (Lnet/minecraft/class_6880;D)Lnet/minecraft/class_5132$class_5133;
  #161 = NameAndType        #159:#160     // method_26868:(Lnet/minecraft/class_6880;D)Lnet/minecraft/class_5132$class_5133;
  #162 = Methodref          #9.#161       // net/minecraft/class_5132$class_5133.method_26868:(Lnet/minecraft/class_6880;D)Lnet/minecraft/class_5132$class_5133;
  #163 = Utf8               field_23719
  #164 = NameAndType        #163:#154     // field_23719:Lnet/minecraft/class_6880;
  #165 = Fieldref           #152.#164     // net/minecraft/class_5134.field_23719:Lnet/minecraft/class_6880;
  #166 = Double             0.16d
  #168 = Utf8               field_23721
  #169 = NameAndType        #168:#154     // field_23721:Lnet/minecraft/class_6880;
  #170 = Fieldref           #152.#169     // net/minecraft/class_5134.field_23721:Lnet/minecraft/class_6880;
  #171 = Double             3.0d
  #173 = Utf8               field_23717
  #174 = NameAndType        #173:#154     // field_23717:Lnet/minecraft/class_6880;
  #175 = Fieldref           #152.#174     // net/minecraft/class_5134.field_23717:Lnet/minecraft/class_6880;
  #176 = Double             10.0d
  #178 = Utf8               field_52450
  #179 = NameAndType        #178:#154     // field_52450:Lnet/minecraft/class_6880;
  #180 = Fieldref           #152.#179     // net/minecraft/class_5134.field_52450:Lnet/minecraft/class_6880;
  #181 = Utf8               setupAnimationStates
  #182 = Utf8               field_6012
  #183 = NameAndType        #182:#26      // field_6012:I
  #184 = Fieldref           #2.#183       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.field_6012:I
  #185 = Utf8               method_41322
  #186 = Utf8               (I)V
  #187 = NameAndType        #185:#186     // method_41322:(I)V
  #188 = Methodref          #48.#187      // net/minecraft/class_7094.method_41322:(I)V
  #189 = Utf8               method_5693
  #190 = Utf8               (Lnet/minecraft/class_2945$class_9222;)V
  #191 = Utf8               builder
  #192 = NameAndType        #189:#190     // method_5693:(Lnet/minecraft/class_2945$class_9222;)V
  #193 = Methodref          #4.#192       // net/minecraft/class_1429.method_5693:(Lnet/minecraft/class_2945$class_9222;)V
  #194 = NameAndType        #27:#28       // COILED_UP:Lnet/minecraft/class_2940;
  #195 = Fieldref           #2.#194       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.COILED_UP:Lnet/minecraft/class_2940;
  #196 = Utf8               java/lang/Boolean
  #197 = Class              #196          // java/lang/Boolean
  #198 = Utf8               valueOf
  #199 = Utf8               (Z)Ljava/lang/Boolean;
  #200 = NameAndType        #198:#199     // valueOf:(Z)Ljava/lang/Boolean;
  #201 = Methodref          #197.#200     // java/lang/Boolean.valueOf:(Z)Ljava/lang/Boolean;
  #202 = Utf8               method_56912
  #203 = Utf8               (Lnet/minecraft/class_2940;Ljava/lang/Object;)Lnet/minecraft/class_2945$class_9222;
  #204 = NameAndType        #202:#203     // method_56912:(Lnet/minecraft/class_2940;Ljava/lang/Object;)Lnet/minecraft/class_2945$class_9222;
  #205 = Methodref          #14.#204      // net/minecraft/class_2945$class_9222.method_56912:(Lnet/minecraft/class_2940;Ljava/lang/Object;)Lnet/minecraft/class_2945$class_9222;
  #206 = NameAndType        #30:#28       // ATTACKING:Lnet/minecraft/class_2940;
  #207 = Fieldref           #2.#206       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.ATTACKING:Lnet/minecraft/class_2940;
  #208 = NameAndType        #31:#28       // VARIANT:Lnet/minecraft/class_2940;
  #209 = Fieldref           #2.#208       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.VARIANT:Lnet/minecraft/class_2940;
  #210 = Utf8               java/lang/Integer
  #211 = Class              #210          // java/lang/Integer
  #212 = Utf8               (I)Ljava/lang/Integer;
  #213 = NameAndType        #198:#212     // valueOf:(I)Ljava/lang/Integer;
  #214 = Methodref          #211.#213     // java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
  #215 = Utf8               Lnet/minecraft/class_2945$class_9222;
  #216 = Utf8               method_5943
  #217 = Utf8               (Lnet/minecraft/class_5425;Lnet/minecraft/class_1266;Lnet/minecraft/class_3730;Lnet/minecraft/class_1315;)Lnet/minecraft/class_1315;
  #218 = Utf8               difficulty
  #219 = Utf8               spawnReason
  #220 = Utf8               entityData
  #221 = Utf8               Lorg/jspecify/annotations/Nullable;
  #222 = Utf8               field_5974
  #223 = Utf8               Lnet/minecraft/class_5819;
  #224 = NameAndType        #222:#223     // field_5974:Lnet/minecraft/class_5819;
  #225 = Fieldref           #2.#224       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.field_5974:Lnet/minecraft/class_5819;
  #226 = Utf8               net/minecraft/class_5819
  #227 = Class              #226          // net/minecraft/class_5819
  #228 = Utf8               method_43057
  #229 = Utf8               ()F
  #230 = NameAndType        #228:#229     // method_43057:()F
  #231 = InterfaceMethodref #227.#230     // net/minecraft/class_5819.method_43057:()F
  #232 = Float              0.5f
  #233 = Utf8               setVariant
  #234 = NameAndType        #233:#186     // setVariant:(I)V
  #235 = Methodref          #2.#234       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.setVariant:(I)V
  #236 = NameAndType        #216:#217     // method_5943:(Lnet/minecraft/class_5425;Lnet/minecraft/class_1266;Lnet/minecraft/class_3730;Lnet/minecraft/class_1315;)Lnet/minecraft/class_1315;
  #237 = Methodref          #4.#236       // net/minecraft/class_1429.method_5943:(Lnet/minecraft/class_5425;Lnet/minecraft/class_1266;Lnet/minecraft/class_3730;Lnet/minecraft/class_1315;)Lnet/minecraft/class_1315;
  #238 = Utf8               Lnet/minecraft/class_5425;
  #239 = Utf8               Lnet/minecraft/class_1266;
  #240 = Utf8               Lnet/minecraft/class_3730;
  #241 = Utf8               Lnet/minecraft/class_1315;
  #242 = Utf8               method_16077
  #243 = Utf8               (Lnet/minecraft/class_3218;Lnet/minecraft/class_1282;Z)V
  #244 = Utf8               source
  #245 = Utf8               causedByPlayer
  #246 = NameAndType        #242:#243     // method_16077:(Lnet/minecraft/class_3218;Lnet/minecraft/class_1282;Z)V
  #247 = Methodref          #4.#246       // net/minecraft/class_1429.method_16077:(Lnet/minecraft/class_3218;Lnet/minecraft/class_1282;Z)V
  #248 = Float              0.2f
  #249 = Utf8               net/minecraft/class_1799
  #250 = Class              #249          // net/minecraft/class_1799
  #251 = Utf8               field_8073
  #252 = NameAndType        #251:#97      // field_8073:Lnet/minecraft/class_1792;
  #253 = Fieldref           #95.#252      // net/minecraft/class_1802.field_8073:Lnet/minecraft/class_1792;
  #254 = Utf8               (Lnet/minecraft/class_1935;I)V
  #255 = NameAndType        #40:#254      // "<init>":(Lnet/minecraft/class_1935;I)V
  #256 = Methodref          #250.#255     // net/minecraft/class_1799."<init>":(Lnet/minecraft/class_1935;I)V
  #257 = Utf8               method_5775
  #258 = Utf8               (Lnet/minecraft/class_3218;Lnet/minecraft/class_1799;)Lnet/minecraft/class_1542;
  #259 = NameAndType        #257:#258     // method_5775:(Lnet/minecraft/class_3218;Lnet/minecraft/class_1799;)Lnet/minecraft/class_1542;
  #260 = Methodref          #2.#259       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.method_5775:(Lnet/minecraft/class_3218;Lnet/minecraft/class_1799;)Lnet/minecraft/class_1542;
  #261 = Float              0.9f
  #262 = Utf8               net/suprk/ufauna/item/ModItems
  #263 = Class              #262          // net/suprk/ufauna/item/ModItems
  #264 = Utf8               RAW_REPTILE_MEAT
  #265 = NameAndType        #264:#97      // RAW_REPTILE_MEAT:Lnet/minecraft/class_1792;
  #266 = Fieldref           #263.#265     // net/suprk/ufauna/item/ModItems.RAW_REPTILE_MEAT:Lnet/minecraft/class_1792;
  #267 = Utf8               Lnet/minecraft/class_3218;
  #268 = Utf8               Lnet/minecraft/class_1282;
  #269 = Utf8               Z
  #270 = Utf8               isPlayerClose
  #271 = Utf8               (D)Z
  #272 = Utf8               distance
  #273 = Utf8               method_73183
  #274 = Utf8               ()Lnet/minecraft/class_1937;
  #275 = NameAndType        #273:#274     // method_73183:()Lnet/minecraft/class_1937;
  #276 = Methodref          #2.#275       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.method_73183:()Lnet/minecraft/class_1937;
  #277 = Utf8               net/minecraft/class_1937
  #278 = Class              #277          // net/minecraft/class_1937
  #279 = Utf8               method_8608
  #280 = Utf8               ()Z
  #281 = NameAndType        #279:#280     // method_8608:()Z
  #282 = Methodref          #278.#281     // net/minecraft/class_1937.method_8608:()Z
  #283 = Utf8               method_18460
  #284 = Utf8               (Lnet/minecraft/class_1297;D)Lnet/minecraft/class_1657;
  #285 = NameAndType        #283:#284     // method_18460:(Lnet/minecraft/class_1297;D)Lnet/minecraft/class_1657;
  #286 = Methodref          #278.#285     // net/minecraft/class_1937.method_18460:(Lnet/minecraft/class_1297;D)Lnet/minecraft/class_1657;
  #287 = Utf8               method_6057
  #288 = Utf8               (Lnet/minecraft/class_1297;)Z
  #289 = NameAndType        #287:#288     // method_6057:(Lnet/minecraft/class_1297;)Z
  #290 = Methodref          #2.#289       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.method_6057:(Lnet/minecraft/class_1297;)Z
  #291 = Utf8               D
  #292 = Utf8               player
  #293 = Utf8               Lnet/minecraft/class_1657;
  #294 = Utf8               method_5773
  #295 = NameAndType        #294:#49      // method_5773:()V
  #296 = Methodref          #4.#295       // net/minecraft/class_1429.method_5773:()V
  #297 = NameAndType        #181:#49      // setupAnimationStates:()V
  #298 = Methodref          #2.#297       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.setupAnimationStates:()V
  #299 = Utf8               method_5968
  #300 = Utf8               ()Lnet/minecraft/class_1309;
  #301 = NameAndType        #299:#300     // method_5968:()Lnet/minecraft/class_1309;
  #302 = Methodref          #2.#301       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.method_5968:()Lnet/minecraft/class_1309;
  #303 = Utf8               net/minecraft/class_1309
  #304 = Class              #303          // net/minecraft/class_1309
  #305 = Utf8               method_19540
  #306 = Utf8               (Z)V
  #307 = NameAndType        #305:#306     // method_19540:(Z)V
  #308 = Methodref          #2.#307       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.method_19540:(Z)V
  #309 = Utf8               method_5858
  #310 = Utf8               (Lnet/minecraft/class_1297;)D
  #311 = NameAndType        #309:#310     // method_5858:(Lnet/minecraft/class_1297;)D
  #312 = Methodref          #2.#311       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.method_5858:(Lnet/minecraft/class_1297;)D
  #313 = Double             8.0d
  #315 = Utf8               method_6109
  #316 = NameAndType        #315:#280     // method_6109:()Z
  #317 = Methodref          #2.#316       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.method_6109:()Z
  #318 = Utf8               net/minecraft/class_3218
  #319 = Class              #318          // net/minecraft/class_3218
  #320 = Utf8               method_6121
  #321 = Utf8               (Lnet/minecraft/class_3218;Lnet/minecraft/class_1297;)Z
  #322 = NameAndType        #320:#321     // method_6121:(Lnet/minecraft/class_3218;Lnet/minecraft/class_1297;)Z
  #323 = Methodref          #2.#322       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.method_6121:(Lnet/minecraft/class_3218;Lnet/minecraft/class_1297;)Z
  #324 = Double             15.0d
  #326 = NameAndType        #270:#271     // isPlayerClose:(D)Z
  #327 = Methodref          #2.#326       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.isPlayerClose:(D)Z
  #328 = Utf8               setCoiledUp
  #329 = NameAndType        #328:#306     // setCoiledUp:(Z)V
  #330 = Methodref          #2.#329       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.setCoiledUp:(Z)V
  #331 = Utf8               isCoiledUp
  #332 = NameAndType        #331:#280     // isCoiledUp:()Z
  #333 = Methodref          #2.#332       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.isCoiledUp:()Z
  #334 = NameAndType        #35:#26       // coiledTicks:I
  #335 = Fieldref           #2.#334       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.coiledTicks:I
  #336 = Float              0.4f
  #337 = Float              0.3f
  #338 = Float              0.85f
  #339 = Utf8               method_23317
  #340 = Utf8               ()D
  #341 = NameAndType        #339:#340     // method_23317:()D
  #342 = Methodref          #2.#341       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.method_23317:()D
  #343 = Utf8               method_23318
  #344 = NameAndType        #343:#340     // method_23318:()D
  #345 = Methodref          #2.#344       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.method_23318:()D
  #346 = Utf8               method_23321
  #347 = NameAndType        #346:#340     // method_23321:()D
  #348 = Methodref          #2.#347       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.method_23321:()D
  #349 = Utf8               net/suprk/ufauna/sound/ModSounds
  #350 = Class              #349          // net/suprk/ufauna/sound/ModSounds
  #351 = Utf8               RATTLESNAKE_RATTLE
  #352 = Utf8               Lnet/minecraft/class_3414;
  #353 = NameAndType        #351:#352     // RATTLESNAKE_RATTLE:Lnet/minecraft/class_3414;
  #354 = Fieldref           #350.#353     // net/suprk/ufauna/sound/ModSounds.RATTLESNAKE_RATTLE:Lnet/minecraft/class_3414;
  #355 = Utf8               net/minecraft/class_3419
  #356 = Class              #355          // net/minecraft/class_3419
  #357 = Utf8               field_15251
  #358 = Utf8               Lnet/minecraft/class_3419;
  #359 = NameAndType        #357:#358     // field_15251:Lnet/minecraft/class_3419;
  #360 = Fieldref           #356.#359     // net/minecraft/class_3419.field_15251:Lnet/minecraft/class_3419;
  #361 = Utf8               method_43128
  #362 = Utf8               (Lnet/minecraft/class_1297;DDDLnet/minecraft/class_3414;Lnet/minecraft/class_3419;FF)V
  #363 = NameAndType        #361:#362     // method_43128:(Lnet/minecraft/class_1297;DDDLnet/minecraft/class_3414;Lnet/minecraft/class_3419;FF)V
  #364 = Methodref          #278.#363     // net/minecraft/class_1937.method_43128:(Lnet/minecraft/class_1297;DDDLnet/minecraft/class_3414;Lnet/minecraft/class_3419;FF)V
  #365 = Utf8               close
  #366 = Utf8               volume
  #367 = Utf8               F
  #368 = Utf8               pitch
  #369 = Utf8               target
  #370 = Utf8               Lnet/minecraft/class_1309;
  #371 = Utf8               getCoiledTicks
  #372 = Utf8               ()I
  #373 = Utf8               getAttackTicks
  #374 = NameAndType        #36:#26       // attackTicks:I
  #375 = Fieldref           #2.#374       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.attackTicks:I
  #376 = Utf8               field_6011
  #377 = Utf8               Lnet/minecraft/class_2945;
  #378 = NameAndType        #376:#377     // field_6011:Lnet/minecraft/class_2945;
  #379 = Fieldref           #2.#378       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.field_6011:Lnet/minecraft/class_2945;
  #380 = Utf8               method_12789
  #381 = Utf8               (Lnet/minecraft/class_2940;)Ljava/lang/Object;
  #382 = NameAndType        #380:#381     // method_12789:(Lnet/minecraft/class_2940;)Ljava/lang/Object;
  #383 = Methodref          #16.#382      // net/minecraft/class_2945.method_12789:(Lnet/minecraft/class_2940;)Ljava/lang/Object;
  #384 = Utf8               booleanValue
  #385 = NameAndType        #384:#280     // booleanValue:()Z
  #386 = Methodref          #197.#385     // java/lang/Boolean.booleanValue:()Z
  #387 = Utf8               input
  #388 = Utf8               method_12778
  #389 = Utf8               (Lnet/minecraft/class_2940;Ljava/lang/Object;)V
  #390 = NameAndType        #388:#389     // method_12778:(Lnet/minecraft/class_2940;Ljava/lang/Object;)V
  #391 = Methodref          #16.#390      // net/minecraft/class_2945.method_12778:(Lnet/minecraft/class_2940;Ljava/lang/Object;)V
  #392 = Utf8               method_6510
  #393 = Utf8               getAttackAnimTicks
  #394 = Utf8               method_6481
  #395 = Utf8               (Lnet/minecraft/class_1799;)Z
  #396 = Utf8               stack
  #397 = Utf8               method_31574
  #398 = Utf8               (Lnet/minecraft/class_1792;)Z
  #399 = NameAndType        #397:#398     // method_31574:(Lnet/minecraft/class_1792;)Z
  #400 = Methodref          #250.#399     // net/minecraft/class_1799.method_31574:(Lnet/minecraft/class_1792;)Z
  #401 = Utf8               Lnet/minecraft/class_1799;
  #402 = Utf8               method_5613
  #403 = Utf8               (Lnet/minecraft/class_3218;Lnet/minecraft/class_1296;)Lnet/minecraft/class_1296;
  #404 = Utf8               entity
  #405 = Utf8               net/suprk/ufauna/entity/ModEntities
  #406 = Class              #405          // net/suprk/ufauna/entity/ModEntities
  #407 = Utf8               RATTLESNAKE
  #408 = NameAndType        #407:#65      // RATTLESNAKE:Lnet/minecraft/class_1299;
  #409 = Fieldref           #406.#408     // net/suprk/ufauna/entity/ModEntities.RATTLESNAKE:Lnet/minecraft/class_1299;
  #410 = Utf8               net/minecraft/class_3730
  #411 = Class              #410          // net/minecraft/class_3730
  #412 = Utf8               field_16466
  #413 = NameAndType        #412:#240     // field_16466:Lnet/minecraft/class_3730;
  #414 = Fieldref           #411.#413     // net/minecraft/class_3730.field_16466:Lnet/minecraft/class_3730;
  #415 = Utf8               net/minecraft/class_1299
  #416 = Class              #415          // net/minecraft/class_1299
  #417 = Utf8               method_5883
  #418 = Utf8               (Lnet/minecraft/class_1937;Lnet/minecraft/class_3730;)Lnet/minecraft/class_1297;
  #419 = NameAndType        #417:#418     // method_5883:(Lnet/minecraft/class_1937;Lnet/minecraft/class_3730;)Lnet/minecraft/class_1297;
  #420 = Methodref          #416.#419     // net/minecraft/class_1299.method_5883:(Lnet/minecraft/class_1937;Lnet/minecraft/class_3730;)Lnet/minecraft/class_1297;
  #421 = Double             0.5d
  #423 = Utf8               getVariant
  #424 = NameAndType        #423:#372     // getVariant:()I
  #425 = Methodref          #2.#424       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.getVariant:()I
  #426 = Utf8               r
  #427 = Utf8               otherParent
  #428 = Utf8               Lnet/minecraft/class_1296;
  #429 = Utf8               child
  #430 = Utf8               method_5979
  #431 = Utf8               (Lnet/minecraft/class_1936;Lnet/minecraft/class_3730;)Z
  #432 = Utf8               field_16465
  #433 = NameAndType        #432:#240     // field_16465:Lnet/minecraft/class_3730;
  #434 = Fieldref           #411.#433     // net/minecraft/class_3730.field_16465:Lnet/minecraft/class_3730;
  #435 = Utf8               field_16469
  #436 = NameAndType        #435:#240     // field_16469:Lnet/minecraft/class_3730;
  #437 = Fieldref           #411.#436     // net/minecraft/class_3730.field_16469:Lnet/minecraft/class_3730;
  #438 = NameAndType        #430:#431     // method_5979:(Lnet/minecraft/class_1936;Lnet/minecraft/class_3730;)Z
  #439 = Methodref          #4.#438       // net/minecraft/class_1429.method_5979:(Lnet/minecraft/class_1936;Lnet/minecraft/class_3730;)Z
  #440 = Utf8               Lnet/minecraft/class_1936;
  #441 = Utf8               method_6047
  #442 = Utf8               ()Lnet/minecraft/class_1799;
  #443 = NameAndType        #441:#442     // method_6047:()Lnet/minecraft/class_1799;
  #444 = Methodref          #117.#443     // net/minecraft/class_1657.method_6047:()Lnet/minecraft/class_1799;
  #445 = Utf8               method_45325
  #446 = Utf8               (Lnet/minecraft/class_6880;)D
  #447 = NameAndType        #445:#446     // method_45325:(Lnet/minecraft/class_6880;)D
  #448 = Methodref          #2.#447       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.method_45325:(Lnet/minecraft/class_6880;)D
  #449 = Utf8               method_48923
  #450 = Utf8               ()Lnet/minecraft/class_8109;
  #451 = NameAndType        #449:#450     // method_48923:()Lnet/minecraft/class_8109;
  #452 = Methodref          #2.#451       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.method_48923:()Lnet/minecraft/class_8109;
  #453 = Utf8               net/minecraft/class_8109
  #454 = Class              #453          // net/minecraft/class_8109
  #455 = Utf8               method_48812
  #456 = Utf8               (Lnet/minecraft/class_1309;)Lnet/minecraft/class_1282;
  #457 = NameAndType        #455:#456     // method_48812:(Lnet/minecraft/class_1309;)Lnet/minecraft/class_1282;
  #458 = Methodref          #454.#457     // net/minecraft/class_8109.method_48812:(Lnet/minecraft/class_1309;)Lnet/minecraft/class_1282;
  #459 = Utf8               method_64397
  #460 = Utf8               (Lnet/minecraft/class_3218;Lnet/minecraft/class_1282;F)Z
  #461 = NameAndType        #459:#460     // method_64397:(Lnet/minecraft/class_3218;Lnet/minecraft/class_1282;F)Z
  #462 = Methodref          #304.#461     // net/minecraft/class_1309.method_64397:(Lnet/minecraft/class_3218;Lnet/minecraft/class_1282;F)Z
  #463 = Float              0.6f
  #464 = Utf8               net/minecraft/class_1293
  #465 = Class              #464          // net/minecraft/class_1293
  #466 = Utf8               net/minecraft/class_1294
  #467 = Class              #466          // net/minecraft/class_1294
  #468 = Utf8               field_5899
  #469 = NameAndType        #468:#154     // field_5899:Lnet/minecraft/class_6880;
  #470 = Fieldref           #467.#469     // net/minecraft/class_1294.field_5899:Lnet/minecraft/class_6880;
  #471 = Utf8               (Lnet/minecraft/class_6880;II)V
  #472 = NameAndType        #40:#471      // "<init>":(Lnet/minecraft/class_6880;II)V
  #473 = Methodref          #465.#472     // net/minecraft/class_1293."<init>":(Lnet/minecraft/class_6880;II)V
  #474 = Utf8               method_6092
  #475 = Utf8               (Lnet/minecraft/class_1293;)Z
  #476 = NameAndType        #474:#475     // method_6092:(Lnet/minecraft/class_1293;)Z
  #477 = Methodref          #304.#476     // net/minecraft/class_1309.method_6092:(Lnet/minecraft/class_1293;)Z
  #478 = Utf8               living
  #479 = Utf8               pt
  #480 = Utf8               Lnet/minecraft/class_1297;
  #481 = Utf8               damage
  #482 = Utf8               success
  #483 = Utf8               canRSpawn
  #484 = Utf8               (Lnet/minecraft/class_1299;Lnet/minecraft/class_5425;Lnet/minecraft/class_3730;Lnet/minecraft/class_2338;Lnet/minecraft/class_5819;)Z
  #485 = Utf8               (Lnet/minecraft/class_1299<Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;>;Lnet/minecraft/class_5425;Lnet/minecraft/class_3730;Lnet/minecraft/class_2338;Lnet/minecraft/class_5819;)Z
  #486 = Utf8               type
  #487 = Utf8               reason
  #488 = Utf8               pos
  #489 = Utf8               random
  #490 = Utf8               net/minecraft/class_5425
  #491 = Class              #490          // net/minecraft/class_5425
  #492 = Utf8               method_22339
  #493 = Utf8               (Lnet/minecraft/class_2338;)I
  #494 = NameAndType        #492:#493     // method_22339:(Lnet/minecraft/class_2338;)I
  #495 = InterfaceMethodref #491.#494     // net/minecraft/class_5425.method_22339:(Lnet/minecraft/class_2338;)I
  #496 = Utf8               net/minecraft/class_2338
  #497 = Class              #496          // net/minecraft/class_2338
  #498 = Utf8               method_10074
  #499 = Utf8               ()Lnet/minecraft/class_2338;
  #500 = NameAndType        #498:#499     // method_10074:()Lnet/minecraft/class_2338;
  #501 = Methodref          #497.#500     // net/minecraft/class_2338.method_10074:()Lnet/minecraft/class_2338;
  #502 = Utf8               method_8320
  #503 = Utf8               (Lnet/minecraft/class_2338;)Lnet/minecraft/class_2680;
  #504 = NameAndType        #502:#503     // method_8320:(Lnet/minecraft/class_2338;)Lnet/minecraft/class_2680;
  #505 = InterfaceMethodref #491.#504     // net/minecraft/class_5425.method_8320:(Lnet/minecraft/class_2338;)Lnet/minecraft/class_2680;
  #506 = Utf8               net/minecraft/class_2680
  #507 = Class              #506          // net/minecraft/class_2680
  #508 = Utf8               method_26212
  #509 = Utf8               (Lnet/minecraft/class_1922;Lnet/minecraft/class_2338;)Z
  #510 = NameAndType        #508:#509     // method_26212:(Lnet/minecraft/class_1922;Lnet/minecraft/class_2338;)Z
  #511 = Methodref          #507.#510     // net/minecraft/class_2680.method_26212:(Lnet/minecraft/class_1922;Lnet/minecraft/class_2338;)Z
  #512 = Utf8               Lnet/minecraft/class_1299<Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;>;
  #513 = Utf8               Lnet/minecraft/class_2338;
  #514 = Utf8               method_55694
  #515 = Utf8               (Lnet/minecraft/class_4050;)Lnet/minecraft/class_4048;
  #516 = Utf8               pose
  #517 = Utf8               net/minecraft/class_4048
  #518 = Class              #517          // net/minecraft/class_4048
  #519 = Utf8               method_18385
  #520 = Utf8               (FF)Lnet/minecraft/class_4048;
  #521 = NameAndType        #519:#520     // method_18385:(FF)Lnet/minecraft/class_4048;
  #522 = Methodref          #518.#521     // net/minecraft/class_4048.method_18385:(FF)Lnet/minecraft/class_4048;
  #523 = Float              0.7f
  #524 = Float              1.25f
  #525 = Float              0.25f
  #526 = Utf8               Lnet/minecraft/class_4050;
  #527 = Utf8               method_5674
  #528 = Utf8               (Lnet/minecraft/class_2940;)V
  #529 = Utf8               (Lnet/minecraft/class_2940<*>;)V
  #530 = Utf8               data
  #531 = NameAndType        #527:#528     // method_5674:(Lnet/minecraft/class_2940;)V
  #532 = Methodref          #4.#531       // net/minecraft/class_1429.method_5674:(Lnet/minecraft/class_2940;)V
  #533 = Utf8               net/minecraft/class_2940
  #534 = Class              #533          // net/minecraft/class_2940
  #535 = Utf8               equals
  #536 = Utf8               (Ljava/lang/Object;)Z
  #537 = NameAndType        #535:#536     // equals:(Ljava/lang/Object;)Z
  #538 = Methodref          #534.#537     // net/minecraft/class_2940.equals:(Ljava/lang/Object;)Z
  #539 = Utf8               method_18382
  #540 = NameAndType        #539:#49      // method_18382:()V
  #541 = Methodref          #2.#540       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.method_18382:()V
  #542 = Utf8               Lnet/minecraft/class_2940<*>;
  #543 = Utf8               method_5992
  #544 = Utf8               (Lnet/minecraft/class_1657;Lnet/minecraft/class_1268;)Lnet/minecraft/class_1269;
  #545 = Utf8               hand
  #546 = Utf8               method_5998
  #547 = Utf8               (Lnet/minecraft/class_1268;)Lnet/minecraft/class_1799;
  #548 = NameAndType        #546:#547     // method_5998:(Lnet/minecraft/class_1268;)Lnet/minecraft/class_1799;
  #549 = Methodref          #117.#548     // net/minecraft/class_1657.method_5998:(Lnet/minecraft/class_1268;)Lnet/minecraft/class_1799;
  #550 = Utf8               field_8469
  #551 = NameAndType        #550:#97      // field_8469:Lnet/minecraft/class_1792;
  #552 = Fieldref           #95.#551      // net/minecraft/class_1802.field_8469:Lnet/minecraft/class_1792;
  #553 = Utf8               method_31549
  #554 = Utf8               ()Lnet/minecraft/class_1656;
  #555 = NameAndType        #553:#554     // method_31549:()Lnet/minecraft/class_1656;
  #556 = Methodref          #117.#555     // net/minecraft/class_1657.method_31549:()Lnet/minecraft/class_1656;
  #557 = Utf8               net/minecraft/class_1656
  #558 = Class              #557          // net/minecraft/class_1656
  #559 = Utf8               field_7477
  #560 = NameAndType        #559:#269     // field_7477:Z
  #561 = Fieldref           #558.#560     // net/minecraft/class_1656.field_7477:Z
  #562 = Utf8               method_7934
  #563 = NameAndType        #562:#186     // method_7934:(I)V
  #564 = Methodref          #250.#563     // net/minecraft/class_1799.method_7934:(I)V
  #565 = Utf8               net/minecraft/class_7923
  #566 = Class              #565          // net/minecraft/class_7923
  #567 = Utf8               field_41178
  #568 = Utf8               Lnet/minecraft/class_7922;
  #569 = NameAndType        #567:#568     // field_41178:Lnet/minecraft/class_7922;
  #570 = Fieldref           #566.#569     // net/minecraft/class_7923.field_41178:Lnet/minecraft/class_7922;
  #571 = Utf8               cubeanimals
  #572 = String             #571          // cubeanimals
  #573 = Utf8               rattlesnake_venom
  #574 = String             #573          // rattlesnake_venom
  #575 = Utf8               net/minecraft/class_2960
  #576 = Class              #575          // net/minecraft/class_2960
  #577 = Utf8               method_60655
  #578 = Utf8               (Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/class_2960;
  #579 = NameAndType        #577:#578     // method_60655:(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/class_2960;
  #580 = Methodref          #576.#579     // net/minecraft/class_2960.method_60655:(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/class_2960;
  #581 = Utf8               net/minecraft/class_7922
  #582 = Class              #581          // net/minecraft/class_7922
  #583 = Utf8               method_63535
  #584 = Utf8               (Lnet/minecraft/class_2960;)Ljava/lang/Object;
  #585 = NameAndType        #583:#584     // method_63535:(Lnet/minecraft/class_2960;)Ljava/lang/Object;
  #586 = InterfaceMethodref #582.#585     // net/minecraft/class_7922.method_63535:(Lnet/minecraft/class_2960;)Ljava/lang/Object;
  #587 = Utf8               (Lnet/minecraft/class_1935;)V
  #588 = NameAndType        #40:#587      // "<init>":(Lnet/minecraft/class_1935;)V
  #589 = Methodref          #250.#588     // net/minecraft/class_1799."<init>":(Lnet/minecraft/class_1935;)V
  #590 = Utf8               method_31548
  #591 = Utf8               ()Lnet/minecraft/class_1661;
  #592 = NameAndType        #590:#591     // method_31548:()Lnet/minecraft/class_1661;
  #593 = Methodref          #117.#592     // net/minecraft/class_1657.method_31548:()Lnet/minecraft/class_1661;
  #594 = Utf8               net/minecraft/class_1661
  #595 = Class              #594          // net/minecraft/class_1661
  #596 = Utf8               method_7394
  #597 = NameAndType        #596:#395     // method_7394:(Lnet/minecraft/class_1799;)Z
  #598 = Methodref          #595.#597     // net/minecraft/class_1661.method_7394:(Lnet/minecraft/class_1799;)Z
  #599 = Utf8               method_7328
  #600 = Utf8               (Lnet/minecraft/class_1799;Z)Lnet/minecraft/class_1542;
  #601 = NameAndType        #599:#600     // method_7328:(Lnet/minecraft/class_1799;Z)Lnet/minecraft/class_1542;
  #602 = Methodref          #117.#601     // net/minecraft/class_1657.method_7328:(Lnet/minecraft/class_1799;Z)Lnet/minecraft/class_1542;
  #603 = Utf8               net/minecraft/class_3417
  #604 = Class              #603          // net/minecraft/class_3417
  #605 = Utf8               field_18054
  #606 = NameAndType        #605:#352     // field_18054:Lnet/minecraft/class_3414;
  #607 = Fieldref           #604.#606     // net/minecraft/class_3417.field_18054:Lnet/minecraft/class_3414;
  #608 = Utf8               method_5783
  #609 = Utf8               (Lnet/minecraft/class_3414;FF)V
  #610 = NameAndType        #608:#609     // method_5783:(Lnet/minecraft/class_3414;FF)V
  #611 = Methodref          #2.#610       // net/suprk/ufauna/entity/custom/RattleSnakeEntity.method_5783:(Lnet/minecraft/class_3414;FF)V
  #612 = Utf8               field_5812
  #613 = Utf8               Lnet/minecraft/class_1269$class_9860;
  #614 = NameAndType        #612:#613     // field_5812:Lnet/minecraft/class_1269$class_9860;
  #615 = Fieldref           #21.#614      // net/minecraft/class_1269.field_5812:Lnet/minecraft/class_1269$class_9860;
  #616 = NameAndType        #543:#544     // method_5992:(Lnet/minecraft/class_1657;Lnet/minecraft/class_1268;)Lnet/minecraft/class_1269;
  #617 = Methodref          #4.#616       // net/minecraft/class_1429.method_5992:(Lnet/minecraft/class_1657;Lnet/minecraft/class_1268;)Lnet/minecraft/class_1269;
  #618 = Utf8               saliva
  #619 = Utf8               Lnet/minecraft/class_1268;
  #620 = Utf8               intValue
  #621 = NameAndType        #620:#372     // intValue:()I
  #622 = Methodref          #211.#621     // java/lang/Integer.intValue:()I
  #623 = Utf8               variant
  #624 = Utf8               method_5652
  #625 = Utf8               (Lnet/minecraft/class_11372;)V
  #626 = Utf8               view
  #627 = NameAndType        #624:#625     // method_5652:(Lnet/minecraft/class_11372;)V
  #628 = Methodref          #4.#627       // net/minecraft/class_1429.method_5652:(Lnet/minecraft/class_11372;)V
  #629 = Utf8               Variant
  #630 = String             #629          // Variant
  #631 = Utf8               net/minecraft/class_11372
  #632 = Class              #631          // net/minecraft/class_11372
  #633 = Utf8               method_71465
  #634 = Utf8               (Ljava/lang/String;I)V
  #635 = NameAndType        #633:#634     // method_71465:(Ljava/lang/String;I)V
  #636 = InterfaceMethodref #632.#635     // net/minecraft/class_11372.method_71465:(Ljava/lang/String;I)V
  #637 = Utf8               Lnet/minecraft/class_11372;
  #638 = Utf8               method_5749
  #639 = Utf8               (Lnet/minecraft/class_11368;)V
  #640 = NameAndType        #638:#639     // method_5749:(Lnet/minecraft/class_11368;)V
  #641 = Methodref          #4.#640       // net/minecraft/class_1429.method_5749:(Lnet/minecraft/class_11368;)V
  #642 = Utf8               net/minecraft/class_11368
  #643 = Class              #642          // net/minecraft/class_11368
  #644 = Utf8               method_71424
  #645 = Utf8               (Ljava/lang/String;I)I
  #646 = NameAndType        #644:#645     // method_71424:(Ljava/lang/String;I)I
  #647 = InterfaceMethodref #643.#646     // net/minecraft/class_11368.method_71424:(Ljava/lang/String;I)I
  #648 = Utf8               Lnet/minecraft/class_11368;
  #649 = Utf8               method_6036
  #650 = Utf8               (Lnet/minecraft/class_1282;F)F
  #651 = Utf8               amount
  #652 = NameAndType        #649:#650     // method_6036:(Lnet/minecraft/class_1282;F)F
  #653 = Methodref          #4.#652       // net/minecraft/class_1429.method_6036:(Lnet/minecraft/class_1282;F)F
  #654 = Utf8               <clinit>
  #655 = Utf8               net/minecraft/class_2943
  #656 = Class              #655          // net/minecraft/class_2943
  #657 = Utf8               field_13323
  #658 = Utf8               Lnet/minecraft/class_2941;
  #659 = NameAndType        #657:#658     // field_13323:Lnet/minecraft/class_2941;
  #660 = Fieldref           #656.#659     // net/minecraft/class_2943.field_13323:Lnet/minecraft/class_2941;
  #661 = Utf8               method_12791
  #662 = Utf8               (Ljava/lang/Class;Lnet/minecraft/class_2941;)Lnet/minecraft/class_2940;
  #663 = NameAndType        #661:#662     // method_12791:(Ljava/lang/Class;Lnet/minecraft/class_2941;)Lnet/minecraft/class_2940;
  #664 = Methodref          #16.#663      // net/minecraft/class_2945.method_12791:(Ljava/lang/Class;Lnet/minecraft/class_2941;)Lnet/minecraft/class_2940;
  #665 = Utf8               field_13327
  #666 = NameAndType        #665:#658     // field_13327:Lnet/minecraft/class_2941;
  #667 = Fieldref           #656.#666     // net/minecraft/class_2943.field_13327:Lnet/minecraft/class_2941;
  #668 = Utf8               Signature
  #669 = Utf8               ConstantValue
  #670 = Utf8               Code
  #671 = Utf8               LineNumberTable
  #672 = Utf8               LocalVariableTable
  #673 = Utf8               LocalVariableTypeTable
  #674 = Utf8               MethodParameters
  #675 = Utf8               StackMapTable
  #676 = Utf8               RuntimeVisibleTypeAnnotations
  #677 = Utf8               InnerClasses
  #678 = Utf8               SourceFile
{
  public final net.minecraft.class_7094 idleAnimationState;
    descriptor: Lnet/minecraft/class_7094;
    flags: (0x0011) ACC_PUBLIC, ACC_FINAL

  private int idleAnimationTimeout;
    descriptor: I
    flags: (0x0002) ACC_PRIVATE

  private static final net.minecraft.class_2940<java.lang.Boolean> COILED_UP;
    descriptor: Lnet/minecraft/class_2940;
    flags: (0x001a) ACC_PRIVATE, ACC_STATIC, ACC_FINAL
    Signature: #29                          // Lnet/minecraft/class_2940<Ljava/lang/Boolean;>;

  private static final net.minecraft.class_2940<java.lang.Boolean> ATTACKING;
    descriptor: Lnet/minecraft/class_2940;
    flags: (0x001a) ACC_PRIVATE, ACC_STATIC, ACC_FINAL
    Signature: #29                          // Lnet/minecraft/class_2940<Ljava/lang/Boolean;>;

  private static final net.minecraft.class_2940<java.lang.Integer> VARIANT;
    descriptor: Lnet/minecraft/class_2940;
    flags: (0x001a) ACC_PRIVATE, ACC_STATIC, ACC_FINAL
    Signature: #32                          // Lnet/minecraft/class_2940<Ljava/lang/Integer;>;

  private int attackCooldown;
    descriptor: I
    flags: (0x0002) ACC_PRIVATE

  private int rattleCooldown;
    descriptor: I
    flags: (0x0002) ACC_PRIVATE

  private int coiledTicks;
    descriptor: I
    flags: (0x0002) ACC_PRIVATE

  private int attackTicks;
    descriptor: I
    flags: (0x0002) ACC_PRIVATE

  private int attackAnimTicks;
    descriptor: I
    flags: (0x0002) ACC_PRIVATE

  private static final int ATTACK_ANIM_DURATION;
    descriptor: I
    flags: (0x001a) ACC_PRIVATE, ACC_STATIC, ACC_FINAL
    ConstantValue: int 8

  public net.suprk.ufauna.entity.custom.RattleSnakeEntity(net.minecraft.class_1299<? extends net.minecraft.class_1429>, net.minecraft.class_1937);
    descriptor: (Lnet/minecraft/class_1299;Lnet/minecraft/class_1937;)V
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=3, locals=3, args_size=3
         0: aload_0
         1: aload_1
         2: aload_2
         3: invokespecial #46                 // Method net/minecraft/class_1429."<init>":(Lnet/minecraft/class_1299;Lnet/minecraft/class_1937;)V
         6: aload_0
         7: new           #48                 // class net/minecraft/class_7094
        10: dup
        11: invokespecial #51                 // Method net/minecraft/class_7094."<init>":()V
        14: putfield      #53                 // Field idleAnimationState:Lnet/minecraft/class_7094;
        17: aload_0
        18: iconst_0
        19: putfield      #55                 // Field idleAnimationTimeout:I
        22: aload_0
        23: iconst_0
        24: putfield      #57                 // Field attackCooldown:I
        27: aload_0
        28: iconst_0
        29: putfield      #59                 // Field rattleCooldown:I
        32: aload_0
        33: iconst_0
        34: putfield      #61                 // Field attackAnimTicks:I
        37: return
      LineNumberTable:
        line 48: 0
        line 44: 6
        line 45: 17
        line 130: 22
        line 131: 27
        line 134: 32
        line 49: 37
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      38     0  this   Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;
            0      38     1 entityType   Lnet/minecraft/class_1299;
            0      38     2 world   Lnet/minecraft/class_1937;
      LocalVariableTypeTable:
        Start  Length  Slot  Name   Signature
            0      38     1 entityType   Lnet/minecraft/class_1299<+Lnet/minecraft/class_1429;>;
    Signature: #42                          // (Lnet/minecraft/class_1299<+Lnet/minecraft/class_1429;>;Lnet/minecraft/class_1937;)V
    MethodParameters:
      Name                           Flags
      entityType
      world

  protected void method_5959();
    descriptor: ()V
    flags: (0x0004) ACC_PROTECTED
    Code:
      stack=11, locals=1, args_size=1
         0: aload_0
         1: getfield      #71                 // Field field_6201:Lnet/minecraft/class_1355;
         4: iconst_0
         5: new           #73                 // class net/minecraft/class_1347
         8: dup
         9: aload_0
        10: invokespecial #76                 // Method net/minecraft/class_1347."<init>":(Lnet/minecraft/class_1308;)V
        13: invokevirtual #82                 // Method net/minecraft/class_1355.method_6277:(ILnet/minecraft/class_1352;)V
        16: aload_0
        17: getfield      #71                 // Field field_6201:Lnet/minecraft/class_1355;
        20: iconst_1
        21: new           #84                 // class net/minecraft/class_1341
        24: dup
        25: aload_0
        26: ldc2_w        #85                 // double 1.15d
        29: invokespecial #89                 // Method net/minecraft/class_1341."<init>":(Lnet/minecraft/class_1429;D)V
        32: invokevirtual #82                 // Method net/minecraft/class_1355.method_6277:(ILnet/minecraft/class_1352;)V
        35: aload_0
        36: getfield      #71                 // Field field_6201:Lnet/minecraft/class_1355;
        39: iconst_2
        40: new           #91                 // class net/minecraft/class_1391
        43: dup
        44: aload_0
        45: ldc2_w        #85                 // double 1.15d
        48: iconst_1
        49: anewarray     #93                 // class net/minecraft/class_1935
        52: dup
        53: iconst_0
        54: getstatic     #99                 // Field net/minecraft/class_1802.field_8504:Lnet/minecraft/class_1792;
        57: aastore
        58: invokestatic  #105                // Method net/minecraft/class_1856.method_8091:([Lnet/minecraft/class_1935;)Lnet/minecraft/class_1856;
        61: iconst_0
        62: invokespecial #108                // Method net/minecraft/class_1391."<init>":(Lnet/minecraft/class_1314;DLjava/util/function/Predicate;Z)V
        65: invokevirtual #82                 // Method net/minecraft/class_1355.method_6277:(ILnet/minecraft/class_1352;)V
        68: aload_0
        69: getfield      #71                 // Field field_6201:Lnet/minecraft/class_1355;
        72: iconst_3
        73: new           #110                // class net/suprk/ufauna/entity/ai/CoilUpGoal
        76: dup
        77: aload_0
        78: invokespecial #113                // Method net/suprk/ufauna/entity/ai/CoilUpGoal."<init>":(Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;)V
        81: invokevirtual #82                 // Method net/minecraft/class_1355.method_6277:(ILnet/minecraft/class_1352;)V
        84: aload_0
        85: getfield      #71                 // Field field_6201:Lnet/minecraft/class_1355;
        88: iconst_4
        89: new           #115                // class net/minecraft/class_1361
        92: dup
        93: aload_0
        94: ldc           #117                // class net/minecraft/class_1657
        96: ldc           #118                // float 15.0f
        98: invokespecial #121                // Method net/minecraft/class_1361."<init>":(Lnet/minecraft/class_1308;Ljava/lang/Class;F)V
       101: invokevirtual #82                 // Method net/minecraft/class_1355.method_6277:(ILnet/minecraft/class_1352;)V
       104: aload_0
       105: getfield      #71                 // Field field_6201:Lnet/minecraft/class_1355;
       108: iconst_5
       109: new           #123                // class net/minecraft/class_1379
       112: dup
       113: aload_0
       114: dconst_1
       115: invokespecial #126                // Method net/minecraft/class_1379."<init>":(Lnet/minecraft/class_1314;D)V
       118: invokevirtual #82                 // Method net/minecraft/class_1355.method_6277:(ILnet/minecraft/class_1352;)V
       121: aload_0
       122: getfield      #71                 // Field field_6201:Lnet/minecraft/class_1355;
       125: bipush        6
       127: new           #128                // class net/minecraft/class_1376
       130: dup
       131: aload_0
       132: invokespecial #129                // Method net/minecraft/class_1376."<init>":(Lnet/minecraft/class_1308;)V
       135: invokevirtual #82                 // Method net/minecraft/class_1355.method_6277:(ILnet/minecraft/class_1352;)V
       138: aload_0
       139: getfield      #132                // Field field_6185:Lnet/minecraft/class_1355;
       142: iconst_0
       143: new           #134                // class net/minecraft/class_1400
       146: dup
       147: aload_0
       148: ldc           #117                // class net/minecraft/class_1657
       150: iconst_1
       151: invokespecial #137                // Method net/minecraft/class_1400."<init>":(Lnet/minecraft/class_1308;Ljava/lang/Class;Z)V
       154: invokevirtual #82                 // Method net/minecraft/class_1355.method_6277:(ILnet/minecraft/class_1352;)V
       157: aload_0
       158: getfield      #132                // Field field_6185:Lnet/minecraft/class_1355;
       161: iconst_1
       162: new           #134                // class net/minecraft/class_1400
       165: dup
       166: aload_0
       167: ldc           #139                // class net/minecraft/class_1463
       169: iconst_1
       170: invokespecial #137                // Method net/minecraft/class_1400."<init>":(Lnet/minecraft/class_1308;Ljava/lang/Class;Z)V
       173: invokevirtual #82                 // Method net/minecraft/class_1355.method_6277:(ILnet/minecraft/class_1352;)V
       176: aload_0
       177: getfield      #132                // Field field_6185:Lnet/minecraft/class_1355;
       180: iconst_2
       181: new           #134                // class net/minecraft/class_1400
       184: dup
       185: aload_0
       186: ldc           #141                // class net/suprk/ufauna/entity/custom/FennecEntity
       188: iconst_1
       189: invokespecial #137                // Method net/minecraft/class_1400."<init>":(Lnet/minecraft/class_1308;Ljava/lang/Class;Z)V
       192: invokevirtual #82                 // Method net/minecraft/class_1355.method_6277:(ILnet/minecraft/class_1352;)V
       195: aload_0
       196: getfield      #132                // Field field_6185:Lnet/minecraft/class_1355;
       199: iconst_3
       200: new           #134                // class net/minecraft/class_1400
       203: dup
       204: aload_0
       205: ldc           #143                // class net/minecraft/class_9069
       207: iconst_1
       208: invokespecial #137                // Method net/minecraft/class_1400."<init>":(Lnet/minecraft/class_1308;Ljava/lang/Class;Z)V
       211: invokevirtual #82                 // Method net/minecraft/class_1355.method_6277:(ILnet/minecraft/class_1352;)V
       214: return
      LineNumberTable:
        line 53: 0
        line 54: 16
        line 55: 35
        line 57: 68
        line 58: 84
        line 59: 104
        line 60: 121
        line 62: 138
        line 63: 157
        line 64: 176
        line 65: 195
        line 66: 214
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0     215     0  this   Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;

  public static net.minecraft.class_5132$class_5133 createAttributes();
    descriptor: ()Lnet/minecraft/class_5132$class_5133;
    flags: (0x0009) ACC_PUBLIC, ACC_STATIC
    Code:
      stack=4, locals=0, args_size=0
         0: invokestatic  #150                // Method net/minecraft/class_1308.method_26828:()Lnet/minecraft/class_5132$class_5133;
         3: getstatic     #156                // Field net/minecraft/class_5134.field_23716:Lnet/minecraft/class_6880;
         6: ldc2_w        #157                // double 12.0d
         9: invokevirtual #162                // Method net/minecraft/class_5132$class_5133.method_26868:(Lnet/minecraft/class_6880;D)Lnet/minecraft/class_5132$class_5133;
        12: getstatic     #165                // Field net/minecraft/class_5134.field_23719:Lnet/minecraft/class_6880;
        15: ldc2_w        #166                // double 0.16d
        18: invokevirtual #162                // Method net/minecraft/class_5132$class_5133.method_26868:(Lnet/minecraft/class_6880;D)Lnet/minecraft/class_5132$class_5133;
        21: getstatic     #170                // Field net/minecraft/class_5134.field_23721:Lnet/minecraft/class_6880;
        24: ldc2_w        #171                // double 3.0d
        27: invokevirtual #162                // Method net/minecraft/class_5132$class_5133.method_26868:(Lnet/minecraft/class_6880;D)Lnet/minecraft/class_5132$class_5133;
        30: getstatic     #175                // Field net/minecraft/class_5134.field_23717:Lnet/minecraft/class_6880;
        33: ldc2_w        #176                // double 10.0d
        36: invokevirtual #162                // Method net/minecraft/class_5132$class_5133.method_26868:(Lnet/minecraft/class_6880;D)Lnet/minecraft/class_5132$class_5133;
        39: getstatic     #180                // Field net/minecraft/class_5134.field_52450:Lnet/minecraft/class_6880;
        42: ldc2_w        #176                // double 10.0d
        45: invokevirtual #162                // Method net/minecraft/class_5132$class_5133.method_26868:(Lnet/minecraft/class_6880;D)Lnet/minecraft/class_5132$class_5133;
        48: areturn
      LineNumberTable:
        line 69: 0
        line 70: 9
        line 71: 18
        line 72: 27
        line 73: 36
        line 74: 45
        line 69: 48

  private void setupAnimationStates();
    descriptor: ()V
    flags: (0x0002) ACC_PRIVATE
    Code:
      stack=3, locals=1, args_size=1
         0: aload_0
         1: getfield      #55                 // Field idleAnimationTimeout:I
         4: ifgt          27
         7: aload_0
         8: bipush        40
        10: putfield      #55                 // Field idleAnimationTimeout:I
        13: aload_0
        14: getfield      #53                 // Field idleAnimationState:Lnet/minecraft/class_7094;
        17: aload_0
        18: getfield      #184                // Field field_6012:I
        21: invokevirtual #188                // Method net/minecraft/class_7094.method_41322:(I)V
        24: goto          37
        27: aload_0
        28: dup
        29: getfield      #55                 // Field idleAnimationTimeout:I
        32: iconst_1
        33: isub
        34: putfield      #55                 // Field idleAnimationTimeout:I
        37: return
      StackMapTable: number_of_entries = 2
        frame_type = 27 /* same */
        frame_type = 9 /* same */
      LineNumberTable:
        line 78: 0
        line 79: 7
        line 80: 13
        line 82: 27
        line 84: 37
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      38     0  this   Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;

  protected void method_5693(net.minecraft.class_2945$class_9222);
    descriptor: (Lnet/minecraft/class_2945$class_9222;)V
    flags: (0x0004) ACC_PROTECTED
    Code:
      stack=3, locals=2, args_size=2
         0: aload_0
         1: aload_1
         2: invokespecial #193                // Method net/minecraft/class_1429.method_5693:(Lnet/minecraft/class_2945$class_9222;)V
         5: aload_1
         6: getstatic     #195                // Field COILED_UP:Lnet/minecraft/class_2940;
         9: iconst_0
        10: invokestatic  #201                // Method java/lang/Boolean.valueOf:(Z)Ljava/lang/Boolean;
        13: invokevirtual #205                // Method net/minecraft/class_2945$class_9222.method_56912:(Lnet/minecraft/class_2940;Ljava/lang/Object;)Lnet/minecraft/class_2945$class_9222;
        16: pop
        17: aload_1
        18: getstatic     #207                // Field ATTACKING:Lnet/minecraft/class_2940;
        21: iconst_0
        22: invokestatic  #201                // Method java/lang/Boolean.valueOf:(Z)Ljava/lang/Boolean;
        25: invokevirtual #205                // Method net/minecraft/class_2945$class_9222.method_56912:(Lnet/minecraft/class_2940;Ljava/lang/Object;)Lnet/minecraft/class_2945$class_9222;
        28: pop
        29: aload_1
        30: getstatic     #209                // Field VARIANT:Lnet/minecraft/class_2940;
        33: iconst_0
        34: invokestatic  #214                // Method java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        37: invokevirtual #205                // Method net/minecraft/class_2945$class_9222.method_56912:(Lnet/minecraft/class_2940;Ljava/lang/Object;)Lnet/minecraft/class_2945$class_9222;
        40: pop
        41: return
      LineNumberTable:
        line 95: 0
        line 96: 5
        line 97: 17
        line 98: 29
        line 99: 41
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      42     0  this   Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;
            0      42     1 builder   Lnet/minecraft/class_2945$class_9222;
    MethodParameters:
      Name                           Flags
      builder

  public net.minecraft.class_1315 method_5943(net.minecraft.class_5425, net.minecraft.class_1266, net.minecraft.class_3730, net.minecraft.class_1315);
    descriptor: (Lnet/minecraft/class_5425;Lnet/minecraft/class_1266;Lnet/minecraft/class_3730;Lnet/minecraft/class_1315;)Lnet/minecraft/class_1315;
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=5, locals=5, args_size=5
         0: aload_0
         1: getfield      #225                // Field field_5974:Lnet/minecraft/class_5819;
         4: invokeinterface #231,  1          // InterfaceMethod net/minecraft/class_5819.method_43057:()F
         9: ldc           #232                // float 0.5f
        11: fcmpg
        12: ifge          23
        15: aload_0
        16: iconst_1
        17: invokevirtual #235                // Method setVariant:(I)V
        20: goto          28
        23: aload_0
        24: iconst_0
        25: invokevirtual #235                // Method setVariant:(I)V
        28: aload_0
        29: aload_1
        30: aload_2
        31: aload_3
        32: aload         4
        34: invokespecial #237                // Method net/minecraft/class_1429.method_5943:(Lnet/minecraft/class_5425;Lnet/minecraft/class_1266;Lnet/minecraft/class_3730;Lnet/minecraft/class_1315;)Lnet/minecraft/class_1315;
        37: areturn
      StackMapTable: number_of_entries = 2
        frame_type = 23 /* same */
        frame_type = 4 /* same */
      LineNumberTable:
        line 104: 0
        line 105: 23
        line 107: 28
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      38     0  this   Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;
            0      38     1 world   Lnet/minecraft/class_5425;
            0      38     2 difficulty   Lnet/minecraft/class_1266;
            0      38     3 spawnReason   Lnet/minecraft/class_3730;
            0      38     4 entityData   Lnet/minecraft/class_1315;
    RuntimeVisibleTypeAnnotations:
      0: #221(): METHOD_RETURN
        org.jspecify.annotations.Nullable
      1: #221(): METHOD_FORMAL_PARAMETER, param_index=3
        org.jspecify.annotations.Nullable
    MethodParameters:
      Name                           Flags
      world
      difficulty
      spawnReason
      entityData

  protected void method_16077(net.minecraft.class_3218, net.minecraft.class_1282, boolean);
    descriptor: (Lnet/minecraft/class_3218;Lnet/minecraft/class_1282;Z)V
    flags: (0x0004) ACC_PROTECTED
    Code:
      stack=6, locals=4, args_size=4
         0: aload_0
         1: aload_1
         2: aload_2
         3: iload_3
         4: invokespecial #247                // Method net/minecraft/class_1429.method_16077:(Lnet/minecraft/class_3218;Lnet/minecraft/class_1282;Z)V
         7: aload_0
         8: getfield      #225                // Field field_5974:Lnet/minecraft/class_5819;
        11: invokeinterface #231,  1          // InterfaceMethod net/minecraft/class_5819.method_43057:()F
        16: ldc           #248                // float 0.2f
        18: fcmpg
        19: ifge          39
        22: aload_0
        23: aload_1
        24: new           #250                // class net/minecraft/class_1799
        27: dup
        28: getstatic     #253                // Field net/minecraft/class_1802.field_8073:Lnet/minecraft/class_1792;
        31: iconst_1
        32: invokespecial #256                // Method net/minecraft/class_1799."<init>":(Lnet/minecraft/class_1935;I)V
        35: invokevirtual #260                // Method method_5775:(Lnet/minecraft/class_3218;Lnet/minecraft/class_1799;)Lnet/minecraft/class_1542;
        38: pop
        39: aload_0
        40: getfield      #225                // Field field_5974:Lnet/minecraft/class_5819;
        43: invokeinterface #231,  1          // InterfaceMethod net/minecraft/class_5819.method_43057:()F
        48: ldc_w         #261                // float 0.9f
        51: fcmpg
        52: ifge          72
        55: aload_0
        56: aload_1
        57: new           #250                // class net/minecraft/class_1799
        60: dup
        61: getstatic     #266                // Field net/suprk/ufauna/item/ModItems.RAW_REPTILE_MEAT:Lnet/minecraft/class_1792;
        64: iconst_1
        65: invokespecial #256                // Method net/minecraft/class_1799."<init>":(Lnet/minecraft/class_1935;I)V
        68: invokevirtual #260                // Method method_5775:(Lnet/minecraft/class_3218;Lnet/minecraft/class_1799;)Lnet/minecraft/class_1542;
        71: pop
        72: return
      StackMapTable: number_of_entries = 2
        frame_type = 39 /* same */
        frame_type = 32 /* same */
      LineNumberTable:
        line 112: 0
        line 114: 7
        line 115: 22
        line 117: 39
        line 118: 55
        line 120: 72
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      73     0  this   Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;
            0      73     1 world   Lnet/minecraft/class_3218;
            0      73     2 source   Lnet/minecraft/class_1282;
            0      73     3 causedByPlayer   Z
    MethodParameters:
      Name                           Flags
      world
      source
      causedByPlayer

  public boolean isPlayerClose(double);
    descriptor: (D)Z
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=4, locals=4, args_size=2
         0: aload_0
         1: invokevirtual #276                // Method method_73183:()Lnet/minecraft/class_1937;
         4: invokevirtual #282                // Method net/minecraft/class_1937.method_8608:()Z
         7: ifeq          12
        10: iconst_0
        11: ireturn
        12: aload_0
        13: invokevirtual #276                // Method method_73183:()Lnet/minecraft/class_1937;
        16: aload_0
        17: dload_1
        18: invokevirtual #286                // Method net/minecraft/class_1937.method_18460:(Lnet/minecraft/class_1297;D)Lnet/minecraft/class_1657;
        21: astore_3
        22: aload_3
        23: ifnull        38
        26: aload_0
        27: aload_3
        28: invokevirtual #290                // Method method_6057:(Lnet/minecraft/class_1297;)Z
        31: ifeq          38
        34: iconst_1
        35: goto          39
        38: iconst_0
        39: ireturn
      StackMapTable: number_of_entries = 3
        frame_type = 12 /* same */
        frame_type = 252 /* append */
          offset_delta = 25
          locals = [ class net/minecraft/class_1657 ]
        frame_type = 64 /* same_locals_1_stack_item */
          stack = [ int ]
      LineNumberTable:
        line 123: 0
        line 125: 12
        line 127: 22
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      40     0  this   Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;
            0      40     1 distance   D
           22      18     3 player   Lnet/minecraft/class_1657;
    MethodParameters:
      Name                           Flags
      distance

  public void method_5773();
    descriptor: ()V
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=12, locals=4, args_size=1
         0: aload_0
         1: invokespecial #296                // Method net/minecraft/class_1429.method_5773:()V
         4: aload_0
         5: invokevirtual #276                // Method method_73183:()Lnet/minecraft/class_1937;
         8: invokevirtual #282                // Method net/minecraft/class_1937.method_8608:()Z
        11: ifeq          19
        14: aload_0
        15: invokevirtual #298                // Method setupAnimationStates:()V
        18: return
        19: aload_0
        20: invokevirtual #302                // Method method_5968:()Lnet/minecraft/class_1309;
        23: astore_1
        24: aload_0
        25: getfield      #57                 // Field attackCooldown:I
        28: ifle          41
        31: aload_0
        32: dup
        33: getfield      #57                 // Field attackCooldown:I
        36: iconst_1
        37: isub
        38: putfield      #57                 // Field attackCooldown:I
        41: aload_0
        42: getfield      #61                 // Field attackAnimTicks:I
        45: ifle          66
        48: aload_0
        49: dup
        50: getfield      #61                 // Field attackAnimTicks:I
        53: iconst_1
        54: isub
        55: putfield      #61                 // Field attackAnimTicks:I
        58: aload_0
        59: iconst_1
        60: invokevirtual #308                // Method method_19540:(Z)V
        63: goto          71
        66: aload_0
        67: iconst_0
        68: invokevirtual #308                // Method method_19540:(Z)V
        71: aload_1
        72: ifnull        126
        75: aload_0
        76: aload_1
        77: invokevirtual #312                // Method method_5858:(Lnet/minecraft/class_1297;)D
        80: ldc2_w        #313                // double 8.0d
        83: dcmpg
        84: ifge          126
        87: aload_0
        88: invokevirtual #317                // Method method_6109:()Z
        91: ifne          126
        94: aload_0
        95: getfield      #57                 // Field attackCooldown:I
        98: ifgt          126
       101: aload_0
       102: aload_0
       103: invokevirtual #276                // Method method_73183:()Lnet/minecraft/class_1937;
       106: checkcast     #319                // class net/minecraft/class_3218
       109: aload_1
       110: invokevirtual #323                // Method method_6121:(Lnet/minecraft/class_3218;Lnet/minecraft/class_1297;)Z
       113: pop
       114: aload_0
       115: bipush        80
       117: putfield      #57                 // Field attackCooldown:I
       120: aload_0
       121: bipush        8
       123: putfield      #61                 // Field attackAnimTicks:I
       126: aload_0
       127: invokevirtual #276                // Method method_73183:()Lnet/minecraft/class_1937;
       130: invokevirtual #282                // Method net/minecraft/class_1937.method_8608:()Z
       133: ifne          149
       136: aload_0
       137: ldc2_w        #324                // double 15.0d
       140: invokevirtual #327                // Method isPlayerClose:(D)Z
       143: istore_2
       144: aload_0
       145: iload_2
       146: invokevirtual #330                // Method setCoiledUp:(Z)V
       149: aload_0
       150: invokevirtual #333                // Method isCoiledUp:()Z
       153: ifeq          270
       156: aload_0
       157: dup
       158: getfield      #335                // Field coiledTicks:I
       161: iconst_1
       162: iadd
       163: putfield      #335                // Field coiledTicks:I
       166: aload_0
       167: getfield      #59                 // Field rattleCooldown:I
       170: ifgt          257
       173: ldc_w         #336                // float 0.4f
       176: aload_0
       177: getfield      #225                // Field field_5974:Lnet/minecraft/class_5819;
       180: invokeinterface #231,  1          // InterfaceMethod net/minecraft/class_5819.method_43057:()F
       185: ldc_w         #337                // float 0.3f
       188: fmul
       189: fadd
       190: fstore_2
       191: ldc_w         #338                // float 0.85f
       194: aload_0
       195: getfield      #225                // Field field_5974:Lnet/minecraft/class_5819;
       198: invokeinterface #231,  1          // InterfaceMethod net/minecraft/class_5819.method_43057:()F
       203: ldc_w         #337                // float 0.3f
       206: fmul
       207: fadd
       208: fstore_3
       209: aload_0
       210: invokevirtual #276                // Method method_73183:()Lnet/minecraft/class_1937;
       213: invokevirtual #282                // Method net/minecraft/class_1937.method_8608:()Z
       216: ifne          247
       219: aload_0
       220: invokevirtual #276                // Method method_73183:()Lnet/minecraft/class_1937;
       223: aconst_null
       224: aload_0
       225: invokevirtual #342                // Method method_23317:()D
       228: aload_0
       229: invokevirtual #345                // Method method_23318:()D
       232: aload_0
       233: invokevirtual #348                // Method method_23321:()D
       236: getstatic     #354                // Field net/suprk/ufauna/sound/ModSounds.RATTLESNAKE_RATTLE:Lnet/minecraft/class_3414;
       239: getstatic     #360                // Field net/minecraft/class_3419.field_15251:Lnet/minecraft/class_3419;
       242: fload_2
       243: fload_3
       244: invokevirtual #364                // Method net/minecraft/class_1937.method_43128:(Lnet/minecraft/class_1297;DDDLnet/minecraft/class_3414;Lnet/minecraft/class_3419;FF)V
       247: aload_0
       248: sipush        200
       251: putfield      #59                 // Field rattleCooldown:I
       254: goto          280
       257: aload_0
       258: dup
       259: getfield      #59                 // Field rattleCooldown:I
       262: iconst_1
       263: isub
       264: putfield      #59                 // Field rattleCooldown:I
       267: goto          280
       270: aload_0
       271: iconst_0
       272: putfield      #59                 // Field rattleCooldown:I
       275: aload_0
       276: iconst_0
       277: putfield      #335                // Field coiledTicks:I
       280: aload_0
       281: invokevirtual #276                // Method method_73183:()Lnet/minecraft/class_1937;
       284: invokevirtual #282                // Method net/minecraft/class_1937.method_8608:()Z
       287: ifeq          294
       290: aload_0
       291: invokevirtual #298                // Method setupAnimationStates:()V
       294: return
      StackMapTable: number_of_entries = 11
        frame_type = 19 /* same */
        frame_type = 252 /* append */
          offset_delta = 21
          locals = [ class net/minecraft/class_1309 ]
        frame_type = 24 /* same */
        frame_type = 4 /* same */
        frame_type = 54 /* same */
        frame_type = 22 /* same */
        frame_type = 253 /* append */
          offset_delta = 97
          locals = [ float, float ]
        frame_type = 249 /* chop */
          offset_delta = 9
        frame_type = 12 /* same */
        frame_type = 9 /* same */
        frame_type = 13 /* same */
      LineNumberTable:
        line 140: 0
        line 142: 4
        line 143: 14
        line 144: 18
        line 147: 19
        line 149: 24
        line 151: 41
        line 152: 48
        line 153: 58
        line 155: 66
        line 158: 71
        line 159: 94
        line 160: 101
        line 161: 114
        line 163: 120
        line 167: 126
        line 168: 136
        line 169: 144
        line 172: 149
        line 174: 156
        line 176: 166
        line 178: 173
        line 179: 191
        line 181: 209
        line 182: 219
        line 184: 225
        line 185: 229
        line 186: 233
        line 182: 244
        line 194: 247
        line 195: 254
        line 196: 257
        line 200: 270
        line 201: 275
        line 204: 280
        line 205: 290
        line 207: 294
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
          144       5     2 close   Z
          191      63     2 volume   F
          209      45     3 pitch   F
            0     295     0  this   Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;
           24     271     1 target   Lnet/minecraft/class_1309;

  public int getCoiledTicks();
    descriptor: ()I
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: getfield      #335                // Field coiledTicks:I
         4: ireturn
      LineNumberTable:
        line 212: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       5     0  this   Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;

  public int getAttackTicks();
    descriptor: ()I
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: getfield      #375                // Field attackTicks:I
         4: ireturn
      LineNumberTable:
        line 216: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       5     0  this   Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;

  public boolean isCoiledUp();
    descriptor: ()Z
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=2, locals=1, args_size=1
         0: aload_0
         1: getfield      #379                // Field field_6011:Lnet/minecraft/class_2945;
         4: getstatic     #195                // Field COILED_UP:Lnet/minecraft/class_2940;
         7: invokevirtual #383                // Method net/minecraft/class_2945.method_12789:(Lnet/minecraft/class_2940;)Ljava/lang/Object;
        10: checkcast     #197                // class java/lang/Boolean
        13: invokevirtual #386                // Method java/lang/Boolean.booleanValue:()Z
        16: ireturn
      LineNumberTable:
        line 220: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      17     0  this   Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;

  public void setCoiledUp(boolean);
    descriptor: (Z)V
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=3, locals=2, args_size=2
         0: aload_0
         1: getfield      #379                // Field field_6011:Lnet/minecraft/class_2945;
         4: getstatic     #195                // Field COILED_UP:Lnet/minecraft/class_2940;
         7: iload_1
         8: invokestatic  #201                // Method java/lang/Boolean.valueOf:(Z)Ljava/lang/Boolean;
        11: invokevirtual #391                // Method net/minecraft/class_2945.method_12778:(Lnet/minecraft/class_2940;Ljava/lang/Object;)V
        14: return
      LineNumberTable:
        line 224: 0
        line 225: 14
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      15     0  this   Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;
            0      15     1 input   Z
    MethodParameters:
      Name                           Flags
      input

  public boolean method_6510();
    descriptor: ()Z
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=2, locals=1, args_size=1
         0: aload_0
         1: getfield      #379                // Field field_6011:Lnet/minecraft/class_2945;
         4: getstatic     #207                // Field ATTACKING:Lnet/minecraft/class_2940;
         7: invokevirtual #383                // Method net/minecraft/class_2945.method_12789:(Lnet/minecraft/class_2940;)Ljava/lang/Object;
        10: checkcast     #197                // class java/lang/Boolean
        13: invokevirtual #386                // Method java/lang/Boolean.booleanValue:()Z
        16: ireturn
      LineNumberTable:
        line 228: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      17     0  this   Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;

  public void method_19540(boolean);
    descriptor: (Z)V
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=3, locals=2, args_size=2
         0: aload_0
         1: getfield      #379                // Field field_6011:Lnet/minecraft/class_2945;
         4: getstatic     #207                // Field ATTACKING:Lnet/minecraft/class_2940;
         7: iload_1
         8: invokestatic  #201                // Method java/lang/Boolean.valueOf:(Z)Ljava/lang/Boolean;
        11: invokevirtual #391                // Method net/minecraft/class_2945.method_12778:(Lnet/minecraft/class_2940;Ljava/lang/Object;)V
        14: return
      LineNumberTable:
        line 232: 0
        line 233: 14
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      15     0  this   Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;
            0      15     1 input   Z
    MethodParameters:
      Name                           Flags
      input

  public int getAttackAnimTicks();
    descriptor: ()I
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: getfield      #61                 // Field attackAnimTicks:I
         4: ireturn
      LineNumberTable:
        line 236: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       5     0  this   Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;

  public boolean method_6481(net.minecraft.class_1799);
    descriptor: (Lnet/minecraft/class_1799;)Z
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=2, locals=2, args_size=2
         0: aload_1
         1: getstatic     #99                 // Field net/minecraft/class_1802.field_8504:Lnet/minecraft/class_1792;
         4: invokevirtual #400                // Method net/minecraft/class_1799.method_31574:(Lnet/minecraft/class_1792;)Z
         7: ireturn
      LineNumberTable:
        line 241: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       8     0  this   Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;
            0       8     1 stack   Lnet/minecraft/class_1799;
    MethodParameters:
      Name                           Flags
      stack

  public net.minecraft.class_1296 method_5613(net.minecraft.class_3218, net.minecraft.class_1296);
    descriptor: (Lnet/minecraft/class_3218;Lnet/minecraft/class_1296;)Lnet/minecraft/class_1296;
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=4, locals=6, args_size=3
         0: getstatic     #409                // Field net/suprk/ufauna/entity/ModEntities.RATTLESNAKE:Lnet/minecraft/class_1299;
         3: aload_1
         4: getstatic     #414                // Field net/minecraft/class_3730.field_16466:Lnet/minecraft/class_3730;
         7: invokevirtual #420                // Method net/minecraft/class_1299.method_5883:(Lnet/minecraft/class_1937;Lnet/minecraft/class_3730;)Lnet/minecraft/class_1297;
        10: checkcast     #2                  // class net/suprk/ufauna/entity/custom/RattleSnakeEntity
        13: astore_3
        14: aload_3
        15: ifnull        72
        18: aload_2
        19: instanceof    #2                  // class net/suprk/ufauna/entity/custom/RattleSnakeEntity
        22: ifeq          72
        25: aload_2
        26: checkcast     #2                  // class net/suprk/ufauna/entity/custom/RattleSnakeEntity
        29: astore        4
        31: aload_0
        32: getfield      #225                // Field field_5974:Lnet/minecraft/class_5819;
        35: invokeinterface #231,  1          // InterfaceMethod net/minecraft/class_5819.method_43057:()F
        40: fstore        5
        42: fload         5
        44: f2d
        45: ldc2_w        #421                // double 0.5d
        48: dcmpg
        49: ifge          63
        52: aload_3
        53: aload_0
        54: invokevirtual #425                // Method getVariant:()I
        57: invokevirtual #235                // Method setVariant:(I)V
        60: goto          72
        63: aload_3
        64: aload         4
        66: invokevirtual #425                // Method getVariant:()I
        69: invokevirtual #235                // Method setVariant:(I)V
        72: aload_3
        73: areturn
      StackMapTable: number_of_entries = 2
        frame_type = 254 /* append */
          offset_delta = 63
          locals = [ class net/suprk/ufauna/entity/custom/RattleSnakeEntity, class net/suprk/ufauna/entity/custom/RattleSnakeEntity, float ]
        frame_type = 249 /* chop */
          offset_delta = 8
      LineNumberTable:
        line 246: 0
        line 248: 14
        line 249: 31
        line 251: 42
        line 252: 52
        line 255: 63
        line 259: 72
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
           42      30     5     r   F
           31      41     4 otherParent   Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;
            0      74     0  this   Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;
            0      74     1 world   Lnet/minecraft/class_3218;
            0      74     2 entity   Lnet/minecraft/class_1296;
           14      60     3 child   Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;
    RuntimeVisibleTypeAnnotations:
      0: #221(): METHOD_RETURN
        org.jspecify.annotations.Nullable
    MethodParameters:
      Name                           Flags
      world
      entity

  public boolean method_5979(net.minecraft.class_1936, net.minecraft.class_3730);
    descriptor: (Lnet/minecraft/class_1936;Lnet/minecraft/class_3730;)Z
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=3, locals=3, args_size=3
         0: aload_2
         1: getstatic     #434                // Field net/minecraft/class_3730.field_16465:Lnet/minecraft/class_3730;
         4: if_acmpeq     23
         7: aload_2
         8: getstatic     #437                // Field net/minecraft/class_3730.field_16469:Lnet/minecraft/class_3730;
        11: if_acmpeq     23
        14: aload_0
        15: aload_1
        16: aload_2
        17: invokespecial #439                // Method net/minecraft/class_1429.method_5979:(Lnet/minecraft/class_1936;Lnet/minecraft/class_3730;)Z
        20: ifeq          27
        23: iconst_1
        24: goto          28
        27: iconst_0
        28: ireturn
      StackMapTable: number_of_entries = 3
        frame_type = 23 /* same */
        frame_type = 3 /* same */
        frame_type = 64 /* same_locals_1_stack_item */
          stack = [ int ]
      LineNumberTable:
        line 264: 0
        line 266: 17
        line 264: 28
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      29     0  this   Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;
            0      29     1 world   Lnet/minecraft/class_1936;
            0      29     2 spawnReason   Lnet/minecraft/class_3730;
    MethodParameters:
      Name                           Flags
      world
      spawnReason

  public boolean method_6121(net.minecraft.class_3218, net.minecraft.class_1297);
    descriptor: (Lnet/minecraft/class_3218;Lnet/minecraft/class_1297;)Z
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=6, locals=6, args_size=3
         0: aload_2
         1: instanceof    #304                // class net/minecraft/class_1309
         4: ifeq          15
         7: aload_2
         8: checkcast     #304                // class net/minecraft/class_1309
        11: astore_3
        12: goto          17
        15: iconst_0
        16: ireturn
        17: aload_2
        18: instanceof    #117                // class net/minecraft/class_1657
        21: ifeq          46
        24: aload_2
        25: checkcast     #117                // class net/minecraft/class_1657
        28: astore        4
        30: aload         4
        32: invokevirtual #444                // Method net/minecraft/class_1657.method_6047:()Lnet/minecraft/class_1799;
        35: getstatic     #99                 // Field net/minecraft/class_1802.field_8504:Lnet/minecraft/class_1792;
        38: invokevirtual #400                // Method net/minecraft/class_1799.method_31574:(Lnet/minecraft/class_1792;)Z
        41: ifeq          46
        44: iconst_0
        45: ireturn
        46: aload_0
        47: getstatic     #170                // Field net/minecraft/class_5134.field_23721:Lnet/minecraft/class_6880;
        50: invokevirtual #448                // Method method_45325:(Lnet/minecraft/class_6880;)D
        53: d2f
        54: fstore        4
        56: aload_3
        57: aload_1
        58: aload_0
        59: invokevirtual #452                // Method method_48923:()Lnet/minecraft/class_8109;
        62: aload_0
        63: invokevirtual #458                // Method net/minecraft/class_8109.method_48812:(Lnet/minecraft/class_1309;)Lnet/minecraft/class_1282;
        66: fload         4
        68: invokevirtual #462                // Method net/minecraft/class_1309.method_64397:(Lnet/minecraft/class_3218;Lnet/minecraft/class_1282;F)Z
        71: istore        5
        73: iload         5
        75: ifeq          149
        78: aload_0
        79: getfield      #225                // Field field_5974:Lnet/minecraft/class_5819;
        82: invokeinterface #231,  1          // InterfaceMethod net/minecraft/class_5819.method_43057:()F
        87: ldc_w         #463                // float 0.6f
        90: fcmpg
        91: ifge          115
        94: aload_3
        95: new           #465                // class net/minecraft/class_1293
        98: dup
        99: getstatic     #470                // Field net/minecraft/class_1294.field_5899:Lnet/minecraft/class_6880;
       102: bipush        100
       104: iconst_0
       105: invokespecial #473                // Method net/minecraft/class_1293."<init>":(Lnet/minecraft/class_6880;II)V
       108: invokevirtual #477                // Method net/minecraft/class_1309.method_6092:(Lnet/minecraft/class_1293;)Z
       111: pop
       112: goto          149
       115: aload_0
       116: getfield      #225                // Field field_5974:Lnet/minecraft/class_5819;
       119: invokeinterface #231,  1          // InterfaceMethod net/minecraft/class_5819.method_43057:()F
       124: ldc           #232                // float 0.5f
       126: fcmpg
       127: ifge          149
       130: aload_3
       131: new           #465                // class net/minecraft/class_1293
       134: dup
       135: getstatic     #470                // Field net/minecraft/class_1294.field_5899:Lnet/minecraft/class_6880;
       138: sipush        200
       141: iconst_0
       142: invokespecial #473                // Method net/minecraft/class_1293."<init>":(Lnet/minecraft/class_6880;II)V
       145: invokevirtual #477                // Method net/minecraft/class_1309.method_6092:(Lnet/minecraft/class_1293;)Z
       148: pop
       149: iload         5
       151: ireturn
      StackMapTable: number_of_entries = 5
        frame_type = 15 /* same */
        frame_type = 252 /* append */
          offset_delta = 1
          locals = [ class net/minecraft/class_1309 ]
        frame_type = 28 /* same */
        frame_type = 253 /* append */
          offset_delta = 68
          locals = [ float, int ]
        frame_type = 33 /* same */
      LineNumberTable:
        line 272: 0
        line 273: 15
        line 276: 17
        line 277: 30
        line 280: 46
        line 282: 56
        line 284: 59
        line 282: 68
        line 288: 73
        line 289: 78
        line 290: 94
        line 297: 115
        line 298: 130
        line 308: 149
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
           12       3     3 living   Lnet/minecraft/class_1309;
           30      16     4    pt   Lnet/minecraft/class_1657;
            0     152     0  this   Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;
            0     152     1 world   Lnet/minecraft/class_3218;
            0     152     2 target   Lnet/minecraft/class_1297;
           17     135     3 living   Lnet/minecraft/class_1309;
           56      96     4 damage   F
           73      79     5 success   Z
    MethodParameters:
      Name                           Flags
      world
      target

  public static boolean canRSpawn(net.minecraft.class_1299<net.suprk.ufauna.entity.custom.RattleSnakeEntity>, net.minecraft.class_5425, net.minecraft.class_3730, net.minecraft.class_2338, net.minecraft.class_5819);
    descriptor: (Lnet/minecraft/class_1299;Lnet/minecraft/class_5425;Lnet/minecraft/class_3730;Lnet/minecraft/class_2338;Lnet/minecraft/class_5819;)Z
    flags: (0x0009) ACC_PUBLIC, ACC_STATIC
    Code:
      stack=3, locals=5, args_size=5
         0: aload_1
         1: aload_3
         2: invokeinterface #495,  2          // InterfaceMethod net/minecraft/class_5425.method_22339:(Lnet/minecraft/class_2338;)I
         7: iconst_4
         8: if_icmple     36
        11: aload_1
        12: aload_3
        13: invokevirtual #501                // Method net/minecraft/class_2338.method_10074:()Lnet/minecraft/class_2338;
        16: invokeinterface #505,  2          // InterfaceMethod net/minecraft/class_5425.method_8320:(Lnet/minecraft/class_2338;)Lnet/minecraft/class_2680;
        21: aload_1
        22: aload_3
        23: invokevirtual #501                // Method net/minecraft/class_2338.method_10074:()Lnet/minecraft/class_2338;
        26: invokevirtual #511                // Method net/minecraft/class_2680.method_26212:(Lnet/minecraft/class_1922;Lnet/minecraft/class_2338;)Z
        29: ifeq          36
        32: iconst_1
        33: goto          37
        36: iconst_0
        37: ireturn
      StackMapTable: number_of_entries = 2
        frame_type = 36 /* same */
        frame_type = 64 /* same_locals_1_stack_item */
          stack = [ int ]
      LineNumberTable:
        line 318: 0
        line 319: 13
        line 318: 37
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      38     0  type   Lnet/minecraft/class_1299;
            0      38     1 world   Lnet/minecraft/class_5425;
            0      38     2 reason   Lnet/minecraft/class_3730;
            0      38     3   pos   Lnet/minecraft/class_2338;
            0      38     4 random   Lnet/minecraft/class_5819;
      LocalVariableTypeTable:
        Start  Length  Slot  Name   Signature
            0      38     0  type   Lnet/minecraft/class_1299<Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;>;
    Signature: #485                         // (Lnet/minecraft/class_1299<Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;>;Lnet/minecraft/class_5425;Lnet/minecraft/class_3730;Lnet/minecraft/class_2338;Lnet/minecraft/class_5819;)Z
    MethodParameters:
      Name                           Flags
      type
      world
      reason
      pos
      random

  protected net.minecraft.class_4048 method_55694(net.minecraft.class_4050);
    descriptor: (Lnet/minecraft/class_4050;)Lnet/minecraft/class_4048;
    flags: (0x0004) ACC_PROTECTED
    Code:
      stack=2, locals=2, args_size=2
         0: aload_0
         1: invokevirtual #333                // Method isCoiledUp:()Z
         4: ifeq          33
         7: aload_0
         8: invokevirtual #317                // Method method_6109:()Z
        11: ifeq          23
        14: ldc_w         #463                // float 0.6f
        17: ldc           #232                // float 0.5f
        19: invokestatic  #522                // Method net/minecraft/class_4048.method_18385:(FF)Lnet/minecraft/class_4048;
        22: areturn
        23: ldc_w         #261                // float 0.9f
        26: ldc_w         #523                // float 0.7f
        29: invokestatic  #522                // Method net/minecraft/class_4048.method_18385:(FF)Lnet/minecraft/class_4048;
        32: areturn
        33: aload_0
        34: invokevirtual #317                // Method method_6109:()Z
        37: ifeq          49
        40: ldc_w         #261                // float 0.9f
        43: ldc           #248                // float 0.2f
        45: invokestatic  #522                // Method net/minecraft/class_4048.method_18385:(FF)Lnet/minecraft/class_4048;
        48: areturn
        49: ldc_w         #524                // float 1.25f
        52: ldc_w         #525                // float 0.25f
        55: invokestatic  #522                // Method net/minecraft/class_4048.method_18385:(FF)Lnet/minecraft/class_4048;
        58: areturn
      StackMapTable: number_of_entries = 3
        frame_type = 23 /* same */
        frame_type = 9 /* same */
        frame_type = 15 /* same */
      LineNumberTable:
        line 324: 0
        line 325: 7
        line 326: 23
        line 329: 33
        line 330: 49
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      59     0  this   Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;
            0      59     1  pose   Lnet/minecraft/class_4050;
    MethodParameters:
      Name                           Flags
      pose

  public void method_5674(net.minecraft.class_2940<?>);
    descriptor: (Lnet/minecraft/class_2940;)V
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=2, locals=2, args_size=2
         0: aload_0
         1: aload_1
         2: invokespecial #532                // Method net/minecraft/class_1429.method_5674:(Lnet/minecraft/class_2940;)V
         5: getstatic     #195                // Field COILED_UP:Lnet/minecraft/class_2940;
         8: aload_1
         9: invokevirtual #538                // Method net/minecraft/class_2940.equals:(Ljava/lang/Object;)Z
        12: ifeq          19
        15: aload_0
        16: invokevirtual #541                // Method method_18382:()V
        19: return
      StackMapTable: number_of_entries = 1
        frame_type = 19 /* same */
      LineNumberTable:
        line 336: 0
        line 338: 5
        line 339: 15
        line 341: 19
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      20     0  this   Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;
            0      20     1  data   Lnet/minecraft/class_2940;
      LocalVariableTypeTable:
        Start  Length  Slot  Name   Signature
            0      20     1  data   Lnet/minecraft/class_2940<*>;
    Signature: #529                         // (Lnet/minecraft/class_2940<*>;)V
    MethodParameters:
      Name                           Flags
      data

  public net.minecraft.class_1269 method_5992(net.minecraft.class_1657, net.minecraft.class_1268);
    descriptor: (Lnet/minecraft/class_1657;Lnet/minecraft/class_1268;)Lnet/minecraft/class_1269;
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=5, locals=5, args_size=3
         0: aload_1
         1: aload_2
         2: invokevirtual #549                // Method net/minecraft/class_1657.method_5998:(Lnet/minecraft/class_1268;)Lnet/minecraft/class_1799;
         5: astore_3
         6: aload_0
         7: invokevirtual #276                // Method method_73183:()Lnet/minecraft/class_1937;
        10: invokevirtual #282                // Method net/minecraft/class_1937.method_8608:()Z
        13: ifne          103
        16: aload_3
        17: getstatic     #552                // Field net/minecraft/class_1802.field_8469:Lnet/minecraft/class_1792;
        20: invokevirtual #400                // Method net/minecraft/class_1799.method_31574:(Lnet/minecraft/class_1792;)Z
        23: ifeq          103
        26: aload_1
        27: invokevirtual #556                // Method net/minecraft/class_1657.method_31549:()Lnet/minecraft/class_1656;
        30: getfield      #561                // Field net/minecraft/class_1656.field_7477:Z
        33: ifne          41
        36: aload_3
        37: iconst_1
        38: invokevirtual #564                // Method net/minecraft/class_1799.method_7934:(I)V
        41: new           #250                // class net/minecraft/class_1799
        44: dup
        45: getstatic     #570                // Field net/minecraft/class_7923.field_41178:Lnet/minecraft/class_7922;
        48: ldc_w         #572                // String cubeanimals
        51: ldc_w         #574                // String rattlesnake_venom
        54: invokestatic  #580                // Method net/minecraft/class_2960.method_60655:(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/class_2960;
        57: invokeinterface #586,  2          // InterfaceMethod net/minecraft/class_7922.method_63535:(Lnet/minecraft/class_2960;)Ljava/lang/Object;
        62: checkcast     #93                 // class net/minecraft/class_1935
        65: invokespecial #589                // Method net/minecraft/class_1799."<init>":(Lnet/minecraft/class_1935;)V
        68: astore        4
        70: aload_1
        71: invokevirtual #593                // Method net/minecraft/class_1657.method_31548:()Lnet/minecraft/class_1661;
        74: aload         4
        76: invokevirtual #598                // Method net/minecraft/class_1661.method_7394:(Lnet/minecraft/class_1799;)Z
        79: ifne          90
        82: aload_1
        83: aload         4
        85: iconst_0
        86: invokevirtual #602                // Method net/minecraft/class_1657.method_7328:(Lnet/minecraft/class_1799;Z)Lnet/minecraft/class_1542;
        89: pop
        90: aload_0
        91: getstatic     #607                // Field net/minecraft/class_3417.field_18054:Lnet/minecraft/class_3414;
        94: fconst_1
        95: fconst_1
        96: invokevirtual #611                // Method method_5783:(Lnet/minecraft/class_3414;FF)V
        99: getstatic     #615                // Field net/minecraft/class_1269.field_5812:Lnet/minecraft/class_1269$class_9860;
       102: areturn
       103: aload_0
       104: aload_1
       105: aload_2
       106: invokespecial #617                // Method net/minecraft/class_1429.method_5992:(Lnet/minecraft/class_1657;Lnet/minecraft/class_1268;)Lnet/minecraft/class_1269;
       109: areturn
      StackMapTable: number_of_entries = 3
        frame_type = 252 /* append */
          offset_delta = 41
          locals = [ class net/minecraft/class_1799 ]
        frame_type = 252 /* append */
          offset_delta = 48
          locals = [ class net/minecraft/class_1799 ]
        frame_type = 250 /* chop */
          offset_delta = 12
      LineNumberTable:
        line 345: 0
        line 347: 6
        line 349: 16
        line 352: 26
        line 353: 36
        line 357: 41
        line 359: 54
        line 358: 57
        line 364: 70
        line 365: 82
        line 368: 90
        line 370: 99
        line 374: 103
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
           70      33     4 saliva   Lnet/minecraft/class_1799;
            0     110     0  this   Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;
            0     110     1 player   Lnet/minecraft/class_1657;
            0     110     2  hand   Lnet/minecraft/class_1268;
            6     104     3 stack   Lnet/minecraft/class_1799;
    MethodParameters:
      Name                           Flags
      player
      hand

  public int getVariant();
    descriptor: ()I
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=2, locals=1, args_size=1
         0: aload_0
         1: getfield      #379                // Field field_6011:Lnet/minecraft/class_2945;
         4: getstatic     #209                // Field VARIANT:Lnet/minecraft/class_2940;
         7: invokevirtual #383                // Method net/minecraft/class_2945.method_12789:(Lnet/minecraft/class_2940;)Ljava/lang/Object;
        10: checkcast     #211                // class java/lang/Integer
        13: invokevirtual #622                // Method java/lang/Integer.intValue:()I
        16: ireturn
      LineNumberTable:
        line 378: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      17     0  this   Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;

  public void setVariant(int);
    descriptor: (I)V
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=3, locals=2, args_size=2
         0: aload_0
         1: getfield      #379                // Field field_6011:Lnet/minecraft/class_2945;
         4: getstatic     #209                // Field VARIANT:Lnet/minecraft/class_2940;
         7: iload_1
         8: invokestatic  #214                // Method java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        11: invokevirtual #391                // Method net/minecraft/class_2945.method_12778:(Lnet/minecraft/class_2940;Ljava/lang/Object;)V
        14: return
      LineNumberTable:
        line 382: 0
        line 383: 14
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      15     0  this   Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;
            0      15     1 variant   I
    MethodParameters:
      Name                           Flags
      variant

  protected void method_5652(net.minecraft.class_11372);
    descriptor: (Lnet/minecraft/class_11372;)V
    flags: (0x0004) ACC_PROTECTED
    Code:
      stack=3, locals=2, args_size=2
         0: aload_0
         1: aload_1
         2: invokespecial #628                // Method net/minecraft/class_1429.method_5652:(Lnet/minecraft/class_11372;)V
         5: aload_1
         6: ldc_w         #630                // String Variant
         9: aload_0
        10: invokevirtual #425                // Method getVariant:()I
        13: invokeinterface #636,  3          // InterfaceMethod net/minecraft/class_11372.method_71465:(Ljava/lang/String;I)V
        18: return
      LineNumberTable:
        line 388: 0
        line 389: 5
        line 390: 18
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      19     0  this   Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;
            0      19     1  view   Lnet/minecraft/class_11372;
    MethodParameters:
      Name                           Flags
      view

  protected void method_5749(net.minecraft.class_11368);
    descriptor: (Lnet/minecraft/class_11368;)V
    flags: (0x0004) ACC_PROTECTED
    Code:
      stack=4, locals=2, args_size=2
         0: aload_0
         1: aload_1
         2: invokespecial #641                // Method net/minecraft/class_1429.method_5749:(Lnet/minecraft/class_11368;)V
         5: aload_0
         6: aload_1
         7: ldc_w         #630                // String Variant
        10: iconst_0
        11: invokeinterface #647,  3          // InterfaceMethod net/minecraft/class_11368.method_71424:(Ljava/lang/String;I)I
        16: invokevirtual #235                // Method setVariant:(I)V
        19: return
      LineNumberTable:
        line 394: 0
        line 395: 5
        line 396: 19
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      20     0  this   Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;
            0      20     1  view   Lnet/minecraft/class_11368;
    MethodParameters:
      Name                           Flags
      view

  protected float method_6036(net.minecraft.class_1282, float);
    descriptor: (Lnet/minecraft/class_1282;F)F
    flags: (0x0004) ACC_PROTECTED
    Code:
      stack=3, locals=3, args_size=3
         0: aload_0
         1: invokevirtual #317                // Method method_6109:()Z
         4: ifeq          11
         7: fload_2
         8: fconst_2
         9: fmul
        10: fstore_2
        11: aload_0
        12: aload_1
        13: fload_2
        14: invokespecial #653                // Method net/minecraft/class_1429.method_6036:(Lnet/minecraft/class_1282;F)F
        17: freturn
      StackMapTable: number_of_entries = 1
        frame_type = 11 /* same */
      LineNumberTable:
        line 400: 0
        line 401: 7
        line 404: 11
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      18     0  this   Lnet/suprk/ufauna/entity/custom/RattleSnakeEntity;
            0      18     1 source   Lnet/minecraft/class_1282;
            0      18     2 amount   F
    MethodParameters:
      Name                           Flags
      source
      amount

  static {};
    descriptor: ()V
    flags: (0x0008) ACC_STATIC
    Code:
      stack=2, locals=0, args_size=0
         0: ldc           #2                  // class net/suprk/ufauna/entity/custom/RattleSnakeEntity
         2: getstatic     #660                // Field net/minecraft/class_2943.field_13323:Lnet/minecraft/class_2941;
         5: invokestatic  #664                // Method net/minecraft/class_2945.method_12791:(Ljava/lang/Class;Lnet/minecraft/class_2941;)Lnet/minecraft/class_2940;
         8: putstatic     #195                // Field COILED_UP:Lnet/minecraft/class_2940;
        11: ldc           #2                  // class net/suprk/ufauna/entity/custom/RattleSnakeEntity
        13: getstatic     #660                // Field net/minecraft/class_2943.field_13323:Lnet/minecraft/class_2941;
        16: invokestatic  #664                // Method net/minecraft/class_2945.method_12791:(Ljava/lang/Class;Lnet/minecraft/class_2941;)Lnet/minecraft/class_2940;
        19: putstatic     #207                // Field ATTACKING:Lnet/minecraft/class_2940;
        22: ldc           #2                  // class net/suprk/ufauna/entity/custom/RattleSnakeEntity
        24: getstatic     #667                // Field net/minecraft/class_2943.field_13327:Lnet/minecraft/class_2941;
        27: invokestatic  #664                // Method net/minecraft/class_2945.method_12791:(Ljava/lang/Class;Lnet/minecraft/class_2941;)Lnet/minecraft/class_2940;
        30: putstatic     #209                // Field VARIANT:Lnet/minecraft/class_2940;
        33: return
      LineNumberTable:
        line 86: 0
        line 87: 5
        line 88: 11
        line 89: 16
        line 90: 22
        line 91: 27
        line 90: 33
}
InnerClasses:
  public static #12= #9 of #11;           // class_5133=class net/minecraft/class_5132$class_5133 of class net/minecraft/class_5132
  public static #17= #14 of #16;          // class_9222=class net/minecraft/class_2945$class_9222 of class net/minecraft/class_2945
  public static final #22= #19 of #21;    // class_9860=class net/minecraft/class_1269$class_9860 of class net/minecraft/class_1269
SourceFile: "RattleSnakeEntity.java"

### net.suprk.ufauna.entity.client.RattleSnake.RattleSnakeRenderState
Classfile jar:file:///tmp/cc146/nested/cubeanimals.jar!/net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.class
  Last modified Sep 25, 2026; size 522 bytes
  SHA-256 checksum 68314c3fb83f68f595f103e9fc8030dc7f71cbd86aff0f71602d357744e5bab3
  Compiled from "RattleSnakeRenderState.java"
public class net.suprk.ufauna.entity.client.RattleSnake.RattleSnakeRenderState extends net.minecraft.class_10042
  minor version: 0
  major version: 65
  flags: (0x0021) ACC_PUBLIC, ACC_SUPER
  this_class: #2                          // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState
  super_class: #4                         // net/minecraft/class_10042
  interfaces: 0, fields: 6, methods: 1, attributes: 1
Constant pool:
   #1 = Utf8               net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState
   #2 = Class              #1             // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState
   #3 = Utf8               net/minecraft/class_10042
   #4 = Class              #3             // net/minecraft/class_10042
   #5 = Utf8               RattleSnakeRenderState.java
   #6 = Utf8               coiledUp
   #7 = Utf8               Z
   #8 = Utf8               partialTicks
   #9 = Utf8               F
  #10 = Utf8               coiledTicks
  #11 = Utf8               I
  #12 = Utf8               attacking
  #13 = Utf8               attackTicks
  #14 = Utf8               variant
  #15 = Utf8               <init>
  #16 = Utf8               ()V
  #17 = NameAndType        #15:#16        // "<init>":()V
  #18 = Methodref          #4.#17         // net/minecraft/class_10042."<init>":()V
  #19 = Utf8               this
  #20 = Utf8               Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;
  #21 = Utf8               Code
  #22 = Utf8               LineNumberTable
  #23 = Utf8               LocalVariableTable
  #24 = Utf8               SourceFile
{
  public boolean coiledUp;
    descriptor: Z
    flags: (0x0001) ACC_PUBLIC

  public float partialTicks;
    descriptor: F
    flags: (0x0001) ACC_PUBLIC

  public int coiledTicks;
    descriptor: I
    flags: (0x0001) ACC_PUBLIC

  public boolean attacking;
    descriptor: Z
    flags: (0x0001) ACC_PUBLIC

  public int attackTicks;
    descriptor: I
    flags: (0x0001) ACC_PUBLIC

  public int variant;
    descriptor: I
    flags: (0x0001) ACC_PUBLIC

  public net.suprk.ufauna.entity.client.RattleSnake.RattleSnakeRenderState();
    descriptor: ()V
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #18                 // Method net/minecraft/class_10042."<init>":()V
         4: return
      LineNumberTable:
        line 5: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       5     0  this   Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;
}
SourceFile: "RattleSnakeRenderState.java"

### net.suprk.ufauna.entity.client.RattleSnake.RattleSnakeModel
Classfile jar:file:///tmp/cc146/nested/cubeanimals.jar!/net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeModel.class
  Last modified Sep 25, 2026; size 7656 bytes
  SHA-256 checksum 5cdbddf987e570cbd84d9899d5a9096ebef306eba59e5863d6483a0ee9005ed5
  Compiled from "RattleSnakeModel.java"
public class net.suprk.ufauna.entity.client.RattleSnake.RattleSnakeModel extends net.minecraft.class_583<net.suprk.ufauna.entity.client.RattleSnake.RattleSnakeRenderState>
  minor version: 0
  major version: 65
  flags: (0x0021) ACC_PUBLIC, ACC_SUPER
  this_class: #2                          // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeModel
  super_class: #5                         // net/minecraft/class_583
  interfaces: 0, fields: 7, methods: 6, attributes: 4
Constant pool:
    #1 = Utf8               net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeModel
    #2 = Class              #1            // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeModel
    #3 = Utf8               Lnet/minecraft/class_583<Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;>;
    #4 = Utf8               net/minecraft/class_583
    #5 = Class              #4            // net/minecraft/class_583
    #6 = Utf8               RattleSnakeModel.java
    #7 = Utf8               java/lang/invoke/MethodHandles$Lookup
    #8 = Class              #7            // java/lang/invoke/MethodHandles$Lookup
    #9 = Utf8               java/lang/invoke/MethodHandles
   #10 = Class              #9            // java/lang/invoke/MethodHandles
   #11 = Utf8               Lookup
   #12 = Utf8               LAYER
   #13 = Utf8               Lnet/minecraft/class_5601;
   #14 = Utf8               root
   #15 = Utf8               Lnet/minecraft/class_630;
   #16 = Utf8               head
   #17 = Utf8               walkingAnimation
   #18 = Utf8               Lnet/minecraft/class_11509;
   #19 = Utf8               idleAnimation
   #20 = Utf8               coiledAnimation
   #21 = Utf8               attackAnimation
   #22 = Utf8               <init>
   #23 = Utf8               (Lnet/minecraft/class_630;)V
   #24 = NameAndType        #22:#23       // "<init>":(Lnet/minecraft/class_630;)V
   #25 = Methodref          #5.#24        // net/minecraft/class_583."<init>":(Lnet/minecraft/class_630;)V
   #26 = NameAndType        #14:#15       // root:Lnet/minecraft/class_630;
   #27 = Fieldref           #2.#26        // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeModel.root:Lnet/minecraft/class_630;
   #28 = Utf8               RattleSnake
   #29 = String             #28           // RattleSnake
   #30 = Utf8               net/minecraft/class_630
   #31 = Class              #30           // net/minecraft/class_630
   #32 = Utf8               method_32086
   #33 = Utf8               (Ljava/lang/String;)Lnet/minecraft/class_630;
   #34 = NameAndType        #32:#33       // method_32086:(Ljava/lang/String;)Lnet/minecraft/class_630;
   #35 = Methodref          #31.#34       // net/minecraft/class_630.method_32086:(Ljava/lang/String;)Lnet/minecraft/class_630;
   #36 = Utf8               Head
   #37 = String             #36           // Head
   #38 = NameAndType        #16:#15       // head:Lnet/minecraft/class_630;
   #39 = Fieldref           #2.#38        // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeModel.head:Lnet/minecraft/class_630;
   #40 = Utf8               net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeAnimations
   #41 = Class              #40           // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeAnimations
   #42 = Utf8               ANIM_RATTLESNAKE_WALK
   #43 = Utf8               Lnet/minecraft/class_7184;
   #44 = NameAndType        #42:#43       // ANIM_RATTLESNAKE_WALK:Lnet/minecraft/class_7184;
   #45 = Fieldref           #41.#44       // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeAnimations.ANIM_RATTLESNAKE_WALK:Lnet/minecraft/class_7184;
   #46 = Utf8               net/minecraft/class_7184
   #47 = Class              #46           // net/minecraft/class_7184
   #48 = Utf8               method_71979
   #49 = Utf8               (Lnet/minecraft/class_630;)Lnet/minecraft/class_11509;
   #50 = NameAndType        #48:#49       // method_71979:(Lnet/minecraft/class_630;)Lnet/minecraft/class_11509;
   #51 = Methodref          #47.#50       // net/minecraft/class_7184.method_71979:(Lnet/minecraft/class_630;)Lnet/minecraft/class_11509;
   #52 = NameAndType        #17:#18       // walkingAnimation:Lnet/minecraft/class_11509;
   #53 = Fieldref           #2.#52        // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeModel.walkingAnimation:Lnet/minecraft/class_11509;
   #54 = Utf8               ANIM_RATTLESNAKE_STAND
   #55 = NameAndType        #54:#43       // ANIM_RATTLESNAKE_STAND:Lnet/minecraft/class_7184;
   #56 = Fieldref           #41.#55       // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeAnimations.ANIM_RATTLESNAKE_STAND:Lnet/minecraft/class_7184;
   #57 = NameAndType        #19:#18       // idleAnimation:Lnet/minecraft/class_11509;
   #58 = Fieldref           #2.#57        // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeModel.idleAnimation:Lnet/minecraft/class_11509;
   #59 = Utf8               ANIM_RATTLESNAKE_COILED_UP
   #60 = NameAndType        #59:#43       // ANIM_RATTLESNAKE_COILED_UP:Lnet/minecraft/class_7184;
   #61 = Fieldref           #41.#60       // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeAnimations.ANIM_RATTLESNAKE_COILED_UP:Lnet/minecraft/class_7184;
   #62 = NameAndType        #20:#18       // coiledAnimation:Lnet/minecraft/class_11509;
   #63 = Fieldref           #2.#62        // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeModel.coiledAnimation:Lnet/minecraft/class_11509;
   #64 = Utf8               ANIM_RATTLESNAKE_ATTACK
   #65 = NameAndType        #64:#43       // ANIM_RATTLESNAKE_ATTACK:Lnet/minecraft/class_7184;
   #66 = Fieldref           #41.#65       // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeAnimations.ANIM_RATTLESNAKE_ATTACK:Lnet/minecraft/class_7184;
   #67 = NameAndType        #21:#18       // attackAnimation:Lnet/minecraft/class_11509;
   #68 = Fieldref           #2.#67        // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeModel.attackAnimation:Lnet/minecraft/class_11509;
   #69 = Utf8               this
   #70 = Utf8               Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeModel;
   #71 = Utf8               setAngles
   #72 = Utf8               (Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;)V
   #73 = Utf8               state
   #74 = Utf8               method_32088
   #75 = Utf8               ()Ljava/util/List;
   #76 = NameAndType        #74:#75       // method_32088:()Ljava/util/List;
   #77 = Methodref          #31.#76       // net/minecraft/class_630.method_32088:()Ljava/util/List;
   #78 = Utf8               (Ljava/lang/Object;)V
   #79 = MethodType         #78           //  (Ljava/lang/Object;)V
   #80 = Utf8               method_41923
   #81 = Utf8               ()V
   #82 = NameAndType        #80:#81       // method_41923:()V
   #83 = Methodref          #31.#82       // net/minecraft/class_630.method_41923:()V
   #84 = MethodHandle       5:#83         // REF_invokeVirtual net/minecraft/class_630.method_41923:()V
   #85 = MethodType         #23           //  (Lnet/minecraft/class_630;)V
   #86 = Utf8               java/lang/invoke/LambdaMetafactory
   #87 = Class              #86           // java/lang/invoke/LambdaMetafactory
   #88 = Utf8               metafactory
   #89 = Utf8               (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
   #90 = NameAndType        #88:#89       // metafactory:(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
   #91 = Methodref          #87.#90       // java/lang/invoke/LambdaMetafactory.metafactory:(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
   #92 = MethodHandle       6:#91         // REF_invokeStatic java/lang/invoke/LambdaMetafactory.metafactory:(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
   #93 = Utf8               accept
   #94 = Utf8               ()Ljava/util/function/Consumer;
   #95 = NameAndType        #93:#94       // accept:()Ljava/util/function/Consumer;
   #96 = InvokeDynamic      #0:#95        // #0:accept:()Ljava/util/function/Consumer;
   #97 = Utf8               java/util/List
   #98 = Class              #97           // java/util/List
   #99 = Utf8               forEach
  #100 = Utf8               (Ljava/util/function/Consumer;)V
  #101 = NameAndType        #99:#100      // forEach:(Ljava/util/function/Consumer;)V
  #102 = InterfaceMethodref #98.#101      // java/util/List.forEach:(Ljava/util/function/Consumer;)V
  #103 = Utf8               net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState
  #104 = Class              #103          // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState
  #105 = Utf8               field_53457
  #106 = Utf8               Z
  #107 = NameAndType        #105:#106     // field_53457:Z
  #108 = Fieldref           #104.#107     // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.field_53457:Z
  #109 = Utf8               org/joml/Vector3f
  #110 = Class              #109          // org/joml/Vector3f
  #111 = Float              -0.4f
  #112 = Utf8               (FFF)V
  #113 = NameAndType        #22:#112      // "<init>":(FFF)V
  #114 = Methodref          #110.#113     // org/joml/Vector3f."<init>":(FFF)V
  #115 = Utf8               method_41924
  #116 = Utf8               (Lorg/joml/Vector3f;)V
  #117 = NameAndType        #115:#116     // method_41924:(Lorg/joml/Vector3f;)V
  #118 = Methodref          #31.#117      // net/minecraft/class_630.method_41924:(Lorg/joml/Vector3f;)V
  #119 = Float              9.6f
  #120 = Utf8               method_41920
  #121 = NameAndType        #120:#116     // method_41920:(Lorg/joml/Vector3f;)V
  #122 = Methodref          #31.#121      // net/minecraft/class_630.method_41920:(Lorg/joml/Vector3f;)V
  #123 = Utf8               field_53447
  #124 = Utf8               F
  #125 = NameAndType        #123:#124     // field_53447:F
  #126 = Fieldref           #104.#125     // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.field_53447:F
  #127 = Float              -60.0f
  #128 = Float              60.0f
  #129 = Utf8               net/minecraft/class_3532
  #130 = Class              #129          // net/minecraft/class_3532
  #131 = Utf8               method_15363
  #132 = Utf8               (FFF)F
  #133 = NameAndType        #131:#132     // method_15363:(FFF)F
  #134 = Methodref          #130.#133     // net/minecraft/class_3532.method_15363:(FFF)F
  #135 = Utf8               field_53448
  #136 = NameAndType        #135:#124     // field_53448:F
  #137 = Fieldref           #104.#136     // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.field_53448:F
  #138 = Float              0.017453292f
  #139 = Utf8               field_3675
  #140 = NameAndType        #139:#124     // field_3675:F
  #141 = Fieldref           #31.#140      // net/minecraft/class_630.field_3675:F
  #142 = Utf8               field_3654
  #143 = NameAndType        #142:#124     // field_3654:F
  #144 = Fieldref           #31.#143      // net/minecraft/class_630.field_3654:F
  #145 = Utf8               attacking
  #146 = NameAndType        #145:#106     // attacking:Z
  #147 = Fieldref           #104.#146     // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.attacking:Z
  #148 = Utf8               attackTicks
  #149 = Utf8               I
  #150 = NameAndType        #148:#149     // attackTicks:I
  #151 = Fieldref           #104.#150     // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.attackTicks:I
  #152 = Utf8               partialTicks
  #153 = NameAndType        #152:#124     // partialTicks:F
  #154 = Fieldref           #104.#153     // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.partialTicks:F
  #155 = Float              20.0f
  #156 = Utf8               net/minecraft/class_11509
  #157 = Class              #156          // net/minecraft/class_11509
  #158 = Utf8               method_71984
  #159 = Utf8               (JF)V
  #160 = NameAndType        #158:#159     // method_71984:(JF)V
  #161 = Methodref          #157.#160     // net/minecraft/class_11509.method_71984:(JF)V
  #162 = Utf8               coiledUp
  #163 = NameAndType        #162:#106     // coiledUp:Z
  #164 = Fieldref           #104.#163     // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.coiledUp:Z
  #165 = Utf8               coiledTicks
  #166 = NameAndType        #165:#149     // coiledTicks:I
  #167 = Fieldref           #104.#166     // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.coiledTicks:I
  #168 = Utf8               field_53451
  #169 = NameAndType        #168:#124     // field_53451:F
  #170 = Fieldref           #104.#169     // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.field_53451:F
  #171 = Float              0.01f
  #172 = Utf8               field_53450
  #173 = NameAndType        #172:#124     // field_53450:F
  #174 = Fieldref           #104.#173     // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.field_53450:F
  #175 = Float              2.5f
  #176 = Utf8               method_71981
  #177 = Utf8               (FFFF)V
  #178 = NameAndType        #176:#177     // method_71981:(FFFF)V
  #179 = Methodref          #157.#178     // net/minecraft/class_11509.method_71981:(FFFF)V
  #180 = Utf8               progressTicks
  #181 = Utf8               Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;
  #182 = Utf8               yawDeg
  #183 = Utf8               pitchDeg
  #184 = Utf8               render
  #185 = Utf8               (Lnet/minecraft/class_4587;Lnet/minecraft/class_4588;IIFFFF)V
  #186 = Utf8               matrices
  #187 = Utf8               vertexConsumer
  #188 = Utf8               light
  #189 = Utf8               overlay
  #190 = Utf8               red
  #191 = Utf8               green
  #192 = Utf8               blue
  #193 = Utf8               alpha
  #194 = Utf8               method_22698
  #195 = Utf8               (Lnet/minecraft/class_4587;Lnet/minecraft/class_4588;II)V
  #196 = NameAndType        #194:#195     // method_22698:(Lnet/minecraft/class_4587;Lnet/minecraft/class_4588;II)V
  #197 = Methodref          #31.#196      // net/minecraft/class_630.method_22698:(Lnet/minecraft/class_4587;Lnet/minecraft/class_4588;II)V
  #198 = Utf8               Lnet/minecraft/class_4587;
  #199 = Utf8               Lnet/minecraft/class_4588;
  #200 = Utf8               getTexturedModelData
  #201 = Utf8               ()Lnet/minecraft/class_5607;
  #202 = Utf8               net/minecraft/class_5609
  #203 = Class              #202          // net/minecraft/class_5609
  #204 = NameAndType        #22:#81       // "<init>":()V
  #205 = Methodref          #203.#204     // net/minecraft/class_5609."<init>":()V
  #206 = Utf8               method_32111
  #207 = Utf8               ()Lnet/minecraft/class_5610;
  #208 = NameAndType        #206:#207     // method_32111:()Lnet/minecraft/class_5610;
  #209 = Methodref          #203.#208     // net/minecraft/class_5609.method_32111:()Lnet/minecraft/class_5610;
  #210 = Utf8               net/minecraft/class_5606
  #211 = Class              #210          // net/minecraft/class_5606
  #212 = Utf8               method_32108
  #213 = Utf8               ()Lnet/minecraft/class_5606;
  #214 = NameAndType        #212:#213     // method_32108:()Lnet/minecraft/class_5606;
  #215 = Methodref          #211.#214     // net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
  #216 = Float              0.5f
  #217 = Float              22.5417f
  #218 = Float              -2.5f
  #219 = Utf8               net/minecraft/class_5603
  #220 = Class              #219          // net/minecraft/class_5603
  #221 = Utf8               method_32090
  #222 = Utf8               (FFF)Lnet/minecraft/class_5603;
  #223 = NameAndType        #221:#222     // method_32090:(FFF)Lnet/minecraft/class_5603;
  #224 = Methodref          #220.#223     // net/minecraft/class_5603.method_32090:(FFF)Lnet/minecraft/class_5603;
  #225 = Utf8               net/minecraft/class_5610
  #226 = Class              #225          // net/minecraft/class_5610
  #227 = Utf8               method_32117
  #228 = Utf8               (Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
  #229 = NameAndType        #227:#228     // method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
  #230 = Methodref          #226.#229     // net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
  #231 = Float              -0.4583f
  #232 = Float              -17.6667f
  #233 = Utf8               Face22
  #234 = String             #233          // Face22
  #235 = Utf8               method_32101
  #236 = Utf8               (II)Lnet/minecraft/class_5606;
  #237 = NameAndType        #235:#236     // method_32101:(II)Lnet/minecraft/class_5606;
  #238 = Methodref          #211.#237     // net/minecraft/class_5606.method_32101:(II)Lnet/minecraft/class_5606;
  #239 = Float              -1.5f
  #240 = Float              -3.75f
  #241 = Float              3.0f
  #242 = Utf8               net/minecraft/class_5605
  #243 = Class              #242          // net/minecraft/class_5605
  #244 = Utf8               (F)V
  #245 = NameAndType        #22:#244      // "<init>":(F)V
  #246 = Methodref          #243.#245     // net/minecraft/class_5605."<init>":(F)V
  #247 = Utf8               method_32098
  #248 = Utf8               (FFFFFFLnet/minecraft/class_5605;)Lnet/minecraft/class_5606;
  #249 = NameAndType        #247:#248     // method_32098:(FFFFFFLnet/minecraft/class_5605;)Lnet/minecraft/class_5606;
  #250 = Methodref          #211.#249     // net/minecraft/class_5606.method_32098:(FFFFFFLnet/minecraft/class_5605;)Lnet/minecraft/class_5606;
  #251 = Float              -2.0f
  #252 = Float              -2.75f
  #253 = Float              4.0f
  #254 = Float              0.9167f
  #255 = Float              -0.0833f
  #256 = Utf8               Face3
  #257 = String             #256          // Face3
  #258 = Float              -0.875f
  #259 = Float              -4.0f
  #260 = Float              -3.0f
  #261 = Float              0.7917f
  #262 = Float              0.1667f
  #263 = Utf8               eys_r1
  #264 = String             #263          // eys_r1
  #265 = Float              -0.25f
  #266 = Float              -0.5f
  #267 = Float              1.25f
  #268 = Float              -1.125f
  #269 = Float              0.3491f
  #270 = Utf8               method_32091
  #271 = Utf8               (FFFFFF)Lnet/minecraft/class_5603;
  #272 = NameAndType        #270:#271     // method_32091:(FFFFFF)Lnet/minecraft/class_5603;
  #273 = Methodref          #220.#272     // net/minecraft/class_5603.method_32091:(FFFFFF)Lnet/minecraft/class_5603;
  #274 = Utf8               eys_r2
  #275 = String             #274          // eys_r2
  #276 = Float              -1.25f
  #277 = Float              -0.3491f
  #278 = Utf8               Tongue
  #279 = String             #278          // Tongue
  #280 = Float              0.0136f
  #281 = Float              0.8333f
  #282 = Float              -0.1212f
  #283 = Utf8               Tongue_r1
  #284 = String             #283          // Tongue_r1
  #285 = Float              -0.0136f
  #286 = Float              -1.7121f
  #287 = Float              1.5708f
  #288 = Float              -0.3927f
  #289 = Utf8               Tongue_r2
  #290 = String             #289          // Tongue_r2
  #291 = Utf8               Tongue_r3
  #292 = String             #291          // Tongue_r3
  #293 = Float              -0.2636f
  #294 = Float              -1.6288f
  #295 = Utf8               Body
  #296 = String             #295          // Body
  #297 = Float              0.4583f
  #298 = Float              -17.5f
  #299 = Utf8               Neck1
  #300 = String             #299          // Neck1
  #301 = Float              -1.0f
  #302 = Utf8               Neck3
  #303 = String             #302          // Neck3
  #304 = Utf8               Neck2
  #305 = String             #304          // Neck2
  #306 = Float              1.5f
  #307 = Utf8               Body6
  #308 = String             #307          // Body6
  #309 = Utf8               Body1
  #310 = String             #309          // Body1
  #311 = Float              6.0f
  #312 = Utf8               Body7
  #313 = String             #312          // Body7
  #314 = Utf8               Body2
  #315 = String             #314          // Body2
  #316 = Float              -6.0f
  #317 = Utf8               Body8
  #318 = String             #317          // Body8
  #319 = Utf8               Body3
  #320 = String             #319          // Body3
  #321 = Float              -0.6477f
  #322 = Float              -3.2081f
  #323 = Float              -0.8523f
  #324 = Float              3.2081f
  #325 = Utf8               Body9
  #326 = String             #325          // Body9
  #327 = Utf8               Body4
  #328 = String             #327          // Body4
  #329 = Utf8               Body10
  #330 = String             #329          // Body10
  #331 = Utf8               Body5
  #332 = String             #331          // Body5
  #333 = Utf8               Tail4
  #334 = String             #333          // Tail4
  #335 = Utf8               Tail1
  #336 = String             #335          // Tail1
  #337 = Utf8               Tail5
  #338 = String             #337          // Tail5
  #339 = Utf8               Tail2
  #340 = String             #339          // Tail2
  #341 = Utf8               Tail6
  #342 = String             #341          // Tail6
  #343 = Utf8               Tail3
  #344 = String             #343          // Tail3
  #345 = Utf8               Rattle
  #346 = String             #345          // Rattle
  #347 = Float              -0.75f
  #348 = Utf8               net/minecraft/class_5607
  #349 = Class              #348          // net/minecraft/class_5607
  #350 = Utf8               method_32110
  #351 = Utf8               (Lnet/minecraft/class_5609;II)Lnet/minecraft/class_5607;
  #352 = NameAndType        #350:#351     // method_32110:(Lnet/minecraft/class_5609;II)Lnet/minecraft/class_5607;
  #353 = Methodref          #349.#352     // net/minecraft/class_5607.method_32110:(Lnet/minecraft/class_5609;II)Lnet/minecraft/class_5607;
  #354 = Utf8               modelData
  #355 = Utf8               Lnet/minecraft/class_5609;
  #356 = Utf8               modelPartData
  #357 = Utf8               Lnet/minecraft/class_5610;
  #358 = Utf8               method_2819
  #359 = NameAndType        #71:#72       // setAngles:(Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;)V
  #360 = Methodref          #2.#359       // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeModel.setAngles:(Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;)V
  #361 = Utf8               <clinit>
  #362 = Utf8               net/minecraft/class_5601
  #363 = Class              #362          // net/minecraft/class_5601
  #364 = Utf8               cubeanimals
  #365 = String             #364          // cubeanimals
  #366 = Utf8               rattlesnake
  #367 = String             #366          // rattlesnake
  #368 = Utf8               net/minecraft/class_2960
  #369 = Class              #368          // net/minecraft/class_2960
  #370 = Utf8               method_60655
  #371 = Utf8               (Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/class_2960;
  #372 = NameAndType        #370:#371     // method_60655:(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/class_2960;
  #373 = Methodref          #369.#372     // net/minecraft/class_2960.method_60655:(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/class_2960;
  #374 = Utf8               main
  #375 = String             #374          // main
  #376 = Utf8               (Lnet/minecraft/class_2960;Ljava/lang/String;)V
  #377 = NameAndType        #22:#376      // "<init>":(Lnet/minecraft/class_2960;Ljava/lang/String;)V
  #378 = Methodref          #363.#377     // net/minecraft/class_5601."<init>":(Lnet/minecraft/class_2960;Ljava/lang/String;)V
  #379 = NameAndType        #12:#13       // LAYER:Lnet/minecraft/class_5601;
  #380 = Fieldref           #2.#379       // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeModel.LAYER:Lnet/minecraft/class_5601;
  #381 = Utf8               Code
  #382 = Utf8               LineNumberTable
  #383 = Utf8               LocalVariableTable
  #384 = Utf8               MethodParameters
  #385 = Utf8               StackMapTable
  #386 = Utf8               InnerClasses
  #387 = Utf8               Signature
  #388 = Utf8               SourceFile
  #389 = Utf8               BootstrapMethods
{
  public static final net.minecraft.class_5601 LAYER;
    descriptor: Lnet/minecraft/class_5601;
    flags: (0x0019) ACC_PUBLIC, ACC_STATIC, ACC_FINAL

  private final net.minecraft.class_630 root;
    descriptor: Lnet/minecraft/class_630;
    flags: (0x0012) ACC_PRIVATE, ACC_FINAL

  private final net.minecraft.class_630 head;
    descriptor: Lnet/minecraft/class_630;
    flags: (0x0012) ACC_PRIVATE, ACC_FINAL

  private final net.minecraft.class_11509 walkingAnimation;
    descriptor: Lnet/minecraft/class_11509;
    flags: (0x0012) ACC_PRIVATE, ACC_FINAL

  private final net.minecraft.class_11509 idleAnimation;
    descriptor: Lnet/minecraft/class_11509;
    flags: (0x0012) ACC_PRIVATE, ACC_FINAL

  private final net.minecraft.class_11509 coiledAnimation;
    descriptor: Lnet/minecraft/class_11509;
    flags: (0x0012) ACC_PRIVATE, ACC_FINAL

  private final net.minecraft.class_11509 attackAnimation;
    descriptor: Lnet/minecraft/class_11509;
    flags: (0x0012) ACC_PRIVATE, ACC_FINAL

  public net.suprk.ufauna.entity.client.RattleSnake.RattleSnakeModel(net.minecraft.class_630);
    descriptor: (Lnet/minecraft/class_630;)V
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=3, locals=2, args_size=2
         0: aload_0
         1: aload_1
         2: invokespecial #25                 // Method net/minecraft/class_583."<init>":(Lnet/minecraft/class_630;)V
         5: aload_0
         6: aload_1
         7: putfield      #27                 // Field root:Lnet/minecraft/class_630;
        10: aload_0
        11: aload_1
        12: ldc           #29                 // String RattleSnake
        14: invokevirtual #35                 // Method net/minecraft/class_630.method_32086:(Ljava/lang/String;)Lnet/minecraft/class_630;
        17: ldc           #37                 // String Head
        19: invokevirtual #35                 // Method net/minecraft/class_630.method_32086:(Ljava/lang/String;)Lnet/minecraft/class_630;
        22: putfield      #39                 // Field head:Lnet/minecraft/class_630;
        25: aload_0
        26: getstatic     #45                 // Field net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeAnimations.ANIM_RATTLESNAKE_WALK:Lnet/minecraft/class_7184;
        29: aload_1
        30: invokevirtual #51                 // Method net/minecraft/class_7184.method_71979:(Lnet/minecraft/class_630;)Lnet/minecraft/class_11509;
        33: putfield      #53                 // Field walkingAnimation:Lnet/minecraft/class_11509;
        36: aload_0
        37: getstatic     #56                 // Field net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeAnimations.ANIM_RATTLESNAKE_STAND:Lnet/minecraft/class_7184;
        40: aload_1
        41: invokevirtual #51                 // Method net/minecraft/class_7184.method_71979:(Lnet/minecraft/class_630;)Lnet/minecraft/class_11509;
        44: putfield      #58                 // Field idleAnimation:Lnet/minecraft/class_11509;
        47: aload_0
        48: getstatic     #61                 // Field net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeAnimations.ANIM_RATTLESNAKE_COILED_UP:Lnet/minecraft/class_7184;
        51: aload_1
        52: invokevirtual #51                 // Method net/minecraft/class_7184.method_71979:(Lnet/minecraft/class_630;)Lnet/minecraft/class_11509;
        55: putfield      #63                 // Field coiledAnimation:Lnet/minecraft/class_11509;
        58: aload_0
        59: getstatic     #66                 // Field net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeAnimations.ANIM_RATTLESNAKE_ATTACK:Lnet/minecraft/class_7184;
        62: aload_1
        63: invokevirtual #51                 // Method net/minecraft/class_7184.method_71979:(Lnet/minecraft/class_630;)Lnet/minecraft/class_11509;
        66: putfield      #68                 // Field attackAnimation:Lnet/minecraft/class_11509;
        69: return
      LineNumberTable:
        line 31: 0
        line 32: 5
        line 33: 10
        line 35: 25
        line 36: 36
        line 37: 47
        line 38: 58
        line 39: 69
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      70     0  this   Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeModel;
            0      70     1  root   Lnet/minecraft/class_630;
    MethodParameters:
      Name                           Flags
      root

  public void setAngles(net.suprk.ufauna.entity.client.RattleSnake.RattleSnakeRenderState);
    descriptor: (Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;)V
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=6, locals=5, args_size=2
         0: aload_0
         1: getfield      #27                 // Field root:Lnet/minecraft/class_630;
         4: invokevirtual #77                 // Method net/minecraft/class_630.method_32088:()Ljava/util/List;
         7: invokedynamic #96,  0             // InvokeDynamic #0:accept:()Ljava/util/function/Consumer;
        12: invokeinterface #102,  2          // InterfaceMethod java/util/List.forEach:(Ljava/util/function/Consumer;)V
        17: aload_1
        18: getfield      #108                // Field net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.field_53457:Z
        21: ifeq          62
        24: aload_0
        25: getfield      #27                 // Field root:Lnet/minecraft/class_630;
        28: new           #110                // class org/joml/Vector3f
        31: dup
        32: ldc           #111                // float -0.4f
        34: ldc           #111                // float -0.4f
        36: ldc           #111                // float -0.4f
        38: invokespecial #114                // Method org/joml/Vector3f."<init>":(FFF)V
        41: invokevirtual #118                // Method net/minecraft/class_630.method_41924:(Lorg/joml/Vector3f;)V
        44: aload_0
        45: getfield      #27                 // Field root:Lnet/minecraft/class_630;
        48: new           #110                // class org/joml/Vector3f
        51: dup
        52: fconst_0
        53: ldc           #119                // float 9.6f
        55: fconst_0
        56: invokespecial #114                // Method org/joml/Vector3f."<init>":(FFF)V
        59: invokevirtual #122                // Method net/minecraft/class_630.method_41920:(Lorg/joml/Vector3f;)V
        62: aload_1
        63: getfield      #126                // Field net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.field_53447:F
        66: ldc           #127                // float -60.0f
        68: ldc           #128                // float 60.0f
        70: invokestatic  #134                // Method net/minecraft/class_3532.method_15363:(FFF)F
        73: fstore_2
        74: aload_1
        75: getfield      #137                // Field net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.field_53448:F
        78: ldc           #127                // float -60.0f
        80: ldc           #128                // float 60.0f
        82: invokestatic  #134                // Method net/minecraft/class_3532.method_15363:(FFF)F
        85: fstore_3
        86: aload_0
        87: getfield      #39                 // Field head:Lnet/minecraft/class_630;
        90: fload_2
        91: ldc           #138                // float 0.017453292f
        93: fmul
        94: putfield      #141                // Field net/minecraft/class_630.field_3675:F
        97: aload_0
        98: getfield      #39                 // Field head:Lnet/minecraft/class_630;
       101: fload_3
       102: ldc           #138                // float 0.017453292f
       104: fmul
       105: putfield      #144                // Field net/minecraft/class_630.field_3654:F
       108: aload_1
       109: getfield      #147                // Field net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.attacking:Z
       112: ifeq          147
       115: bipush        12
       117: aload_1
       118: getfield      #151                // Field net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.attackTicks:I
       121: isub
       122: i2f
       123: aload_1
       124: getfield      #154                // Field net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.partialTicks:F
       127: fadd
       128: fstore        4
       130: aload_0
       131: getfield      #68                 // Field attackAnimation:Lnet/minecraft/class_11509;
       134: fload         4
       136: ldc           #155                // float 20.0f
       138: fmul
       139: f2l
       140: fconst_1
       141: invokevirtual #161                // Method net/minecraft/class_11509.method_71984:(JF)V
       144: goto          221
       147: aload_1
       148: getfield      #164                // Field net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.coiledUp:Z
       151: ifeq          179
       154: aload_0
       155: getfield      #63                 // Field coiledAnimation:Lnet/minecraft/class_11509;
       158: aload_1
       159: getfield      #167                // Field net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.coiledTicks:I
       162: i2f
       163: aload_1
       164: getfield      #154                // Field net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.partialTicks:F
       167: fadd
       168: ldc           #155                // float 20.0f
       170: fmul
       171: f2l
       172: fconst_1
       173: invokevirtual #161                // Method net/minecraft/class_11509.method_71984:(JF)V
       176: goto          221
       179: aload_1
       180: getfield      #170                // Field net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.field_53451:F
       183: ldc           #171                // float 0.01f
       185: fcmpl
       186: ifle          212
       189: aload_0
       190: getfield      #53                 // Field walkingAnimation:Lnet/minecraft/class_11509;
       193: aload_1
       194: getfield      #174                // Field net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.field_53450:F
       197: fconst_2
       198: fmul
       199: aload_1
       200: getfield      #170                // Field net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState.field_53451:F
       203: fconst_2
       204: ldc           #175                // float 2.5f
       206: invokevirtual #179                // Method net/minecraft/class_11509.method_71981:(FFFF)V
       209: goto          221
       212: aload_0
       213: getfield      #58                 // Field idleAnimation:Lnet/minecraft/class_11509;
       216: lconst_0
       217: fconst_1
       218: invokevirtual #161                // Method net/minecraft/class_11509.method_71984:(JF)V
       221: return
      StackMapTable: number_of_entries = 5
        frame_type = 62 /* same */
        frame_type = 253 /* append */
          offset_delta = 84
          locals = [ float, float ]
        frame_type = 31 /* same */
        frame_type = 32 /* same */
        frame_type = 8 /* same */
      LineNumberTable:
        line 43: 0
        line 45: 17
        line 46: 24
        line 48: 44
        line 52: 62
        line 53: 74
        line 56: 86
        line 57: 97
        line 59: 108
        line 60: 115
        line 61: 130
        line 62: 144
        line 63: 147
        line 64: 154
        line 66: 179
        line 67: 189
        line 75: 212
        line 77: 221
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
          130      14     4 progressTicks   F
            0     222     0  this   Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeModel;
            0     222     1 state   Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;
           74     148     2 yawDeg   F
           86     136     3 pitchDeg   F
    MethodParameters:
      Name                           Flags
      state

  public void render(net.minecraft.class_4587, net.minecraft.class_4588, int, int, float, float, float, float);
    descriptor: (Lnet/minecraft/class_4587;Lnet/minecraft/class_4588;IIFFFF)V
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=5, locals=9, args_size=9
         0: aload_0
         1: getfield      #27                 // Field root:Lnet/minecraft/class_630;
         4: aload_1
         5: aload_2
         6: iload_3
         7: iload         4
         9: invokevirtual #197                // Method net/minecraft/class_630.method_22698:(Lnet/minecraft/class_4587;Lnet/minecraft/class_4588;II)V
        12: return
      LineNumberTable:
        line 80: 0
        line 81: 12
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      13     0  this   Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeModel;
            0      13     1 matrices   Lnet/minecraft/class_4587;
            0      13     2 vertexConsumer   Lnet/minecraft/class_4588;
            0      13     3 light   I
            0      13     4 overlay   I
            0      13     5   red   F
            0      13     6 green   F
            0      13     7  blue   F
            0      13     8 alpha   F
    MethodParameters:
      Name                           Flags
      matrices
      vertexConsumer
      light
      overlay
      red
      green
      blue
      alpha

  public static net.minecraft.class_5607 getTexturedModelData();
    descriptor: ()Lnet/minecraft/class_5607;
    flags: (0x0009) ACC_PUBLIC, ACC_STATIC
    Code:
      stack=12, locals=33, args_size=0
         0: new           #203                // class net/minecraft/class_5609
         3: dup
         4: invokespecial #205                // Method net/minecraft/class_5609."<init>":()V
         7: astore_0
         8: aload_0
         9: invokevirtual #209                // Method net/minecraft/class_5609.method_32111:()Lnet/minecraft/class_5610;
        12: astore_1
        13: aload_1
        14: ldc           #29                 // String RattleSnake
        16: invokestatic  #215                // Method net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
        19: ldc           #216                // float 0.5f
        21: ldc           #217                // float 22.5417f
        23: ldc           #218                // float -2.5f
        25: invokestatic  #224                // Method net/minecraft/class_5603.method_32090:(FFF)Lnet/minecraft/class_5603;
        28: invokevirtual #230                // Method net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
        31: astore_2
        32: aload_2
        33: ldc           #37                 // String Head
        35: invokestatic  #215                // Method net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
        38: fconst_0
        39: ldc           #231                // float -0.4583f
        41: ldc           #232                // float -17.6667f
        43: invokestatic  #224                // Method net/minecraft/class_5603.method_32090:(FFF)Lnet/minecraft/class_5603;
        46: invokevirtual #230                // Method net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
        49: astore_3
        50: aload_3
        51: ldc           #234                // String Face22
        53: invokestatic  #215                // Method net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
        56: bipush        30
        58: bipush        19
        60: invokevirtual #238                // Method net/minecraft/class_5606.method_32101:(II)Lnet/minecraft/class_5606;
        63: ldc           #239                // float -1.5f
        65: fconst_0
        66: ldc           #240                // float -3.75f
        68: ldc           #241                // float 3.0f
        70: fconst_1
        71: fconst_1
        72: new           #243                // class net/minecraft/class_5605
        75: dup
        76: fconst_0
        77: invokespecial #246                // Method net/minecraft/class_5605."<init>":(F)V
        80: invokevirtual #250                // Method net/minecraft/class_5606.method_32098:(FFFFFFLnet/minecraft/class_5605;)Lnet/minecraft/class_5606;
        83: iconst_0
        84: bipush        26
        86: invokevirtual #238                // Method net/minecraft/class_5606.method_32101:(II)Lnet/minecraft/class_5606;
        89: ldc           #251                // float -2.0f
        91: fconst_0
        92: ldc           #252                // float -2.75f
        94: ldc           #253                // float 4.0f
        96: fconst_1
        97: ldc           #241                // float 3.0f
        99: new           #243                // class net/minecraft/class_5605
       102: dup
       103: fconst_0
       104: invokespecial #246                // Method net/minecraft/class_5605."<init>":(F)V
       107: invokevirtual #250                // Method net/minecraft/class_5606.method_32098:(FFFFFFLnet/minecraft/class_5605;)Lnet/minecraft/class_5606;
       110: fconst_0
       111: ldc           #254                // float 0.9167f
       113: ldc           #255                // float -0.0833f
       115: invokestatic  #224                // Method net/minecraft/class_5603.method_32090:(FFF)Lnet/minecraft/class_5603;
       118: invokevirtual #230                // Method net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
       121: astore        4
       123: aload_3
       124: ldc_w         #257                // String Face3
       127: invokestatic  #215                // Method net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
       130: bipush        30
       132: bipush        21
       134: invokevirtual #238                // Method net/minecraft/class_5606.method_32101:(II)Lnet/minecraft/class_5606;
       137: ldc           #239                // float -1.5f
       139: ldc_w         #258                // float -0.875f
       142: ldc_w         #259                // float -4.0f
       145: ldc           #241                // float 3.0f
       147: fconst_1
       148: fconst_1
       149: new           #243                // class net/minecraft/class_5605
       152: dup
       153: fconst_0
       154: invokespecial #246                // Method net/minecraft/class_5605."<init>":(F)V
       157: invokevirtual #250                // Method net/minecraft/class_5606.method_32098:(FFFFFFLnet/minecraft/class_5605;)Lnet/minecraft/class_5606;
       160: bipush        14
       162: bipush        27
       164: invokevirtual #238                // Method net/minecraft/class_5606.method_32101:(II)Lnet/minecraft/class_5606;
       167: ldc           #251                // float -2.0f
       169: ldc_w         #258                // float -0.875f
       172: ldc_w         #260                // float -3.0f
       175: ldc           #253                // float 4.0f
       177: fconst_1
       178: ldc           #241                // float 3.0f
       180: new           #243                // class net/minecraft/class_5605
       183: dup
       184: fconst_0
       185: invokespecial #246                // Method net/minecraft/class_5605."<init>":(F)V
       188: invokevirtual #250                // Method net/minecraft/class_5606.method_32098:(FFFFFFLnet/minecraft/class_5605;)Lnet/minecraft/class_5606;
       191: fconst_0
       192: ldc_w         #261                // float 0.7917f
       195: ldc_w         #262                // float 0.1667f
       198: invokestatic  #224                // Method net/minecraft/class_5603.method_32090:(FFF)Lnet/minecraft/class_5603;
       201: invokevirtual #230                // Method net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
       204: astore        5
       206: aload         5
       208: ldc_w         #264                // String eys_r1
       211: invokestatic  #215                // Method net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
       214: bipush        10
       216: bipush        30
       218: invokevirtual #238                // Method net/minecraft/class_5606.method_32101:(II)Lnet/minecraft/class_5606;
       221: ldc_w         #265                // float -0.25f
       224: ldc_w         #265                // float -0.25f
       227: ldc_w         #266                // float -0.5f
       230: ldc           #216                // float 0.5f
       232: ldc           #216                // float 0.5f
       234: fconst_1
       235: new           #243                // class net/minecraft/class_5605
       238: dup
       239: fconst_0
       240: invokespecial #246                // Method net/minecraft/class_5605."<init>":(F)V
       243: invokevirtual #250                // Method net/minecraft/class_5606.method_32098:(FFFFFFLnet/minecraft/class_5605;)Lnet/minecraft/class_5606;
       246: ldc_w         #267                // float 1.25f
       249: ldc_w         #268                // float -1.125f
       252: ldc_w         #260                // float -3.0f
       255: fconst_0
       256: ldc_w         #269                // float 0.3491f
       259: fconst_0
       260: invokestatic  #273                // Method net/minecraft/class_5603.method_32091:(FFFFFF)Lnet/minecraft/class_5603;
       263: invokevirtual #230                // Method net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
       266: astore        6
       268: aload         5
       270: ldc_w         #275                // String eys_r2
       273: invokestatic  #215                // Method net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
       276: bipush        30
       278: bipush        23
       280: invokevirtual #238                // Method net/minecraft/class_5606.method_32101:(II)Lnet/minecraft/class_5606;
       283: ldc_w         #265                // float -0.25f
       286: ldc_w         #265                // float -0.25f
       289: ldc_w         #266                // float -0.5f
       292: ldc           #216                // float 0.5f
       294: ldc           #216                // float 0.5f
       296: fconst_1
       297: new           #243                // class net/minecraft/class_5605
       300: dup
       301: fconst_0
       302: invokespecial #246                // Method net/minecraft/class_5605."<init>":(F)V
       305: invokevirtual #250                // Method net/minecraft/class_5606.method_32098:(FFFFFFLnet/minecraft/class_5605;)Lnet/minecraft/class_5606;
       308: ldc_w         #276                // float -1.25f
       311: ldc_w         #268                // float -1.125f
       314: ldc_w         #260                // float -3.0f
       317: fconst_0
       318: ldc_w         #277                // float -0.3491f
       321: fconst_0
       322: invokestatic  #273                // Method net/minecraft/class_5603.method_32091:(FFFFFF)Lnet/minecraft/class_5603;
       325: invokevirtual #230                // Method net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
       328: astore        7
       330: aload_3
       331: ldc_w         #279                // String Tongue
       334: invokestatic  #215                // Method net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
       337: ldc_w         #280                // float 0.0136f
       340: ldc_w         #281                // float 0.8333f
       343: ldc_w         #282                // float -0.1212f
       346: invokestatic  #224                // Method net/minecraft/class_5603.method_32090:(FFF)Lnet/minecraft/class_5603;
       349: invokevirtual #230                // Method net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
       352: astore        8
       354: aload         8
       356: ldc_w         #284                // String Tongue_r1
       359: invokestatic  #215                // Method net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
       362: iconst_2
       363: iconst_2
       364: invokevirtual #238                // Method net/minecraft/class_5606.method_32101:(II)Lnet/minecraft/class_5606;
       367: ldc_w         #265                // float -0.25f
       370: ldc           #251                // float -2.0f
       372: fconst_0
       373: ldc           #216                // float 0.5f
       375: fconst_2
       376: fconst_0
       377: new           #243                // class net/minecraft/class_5605
       380: dup
       381: fconst_0
       382: invokespecial #246                // Method net/minecraft/class_5605."<init>":(F)V
       385: invokevirtual #250                // Method net/minecraft/class_5606.method_32098:(FFFFFFLnet/minecraft/class_5605;)Lnet/minecraft/class_5606;
       388: ldc_w         #285                // float -0.0136f
       391: fconst_0
       392: ldc_w         #286                // float -1.7121f
       395: ldc_w         #287                // float 1.5708f
       398: ldc_w         #288                // float -0.3927f
       401: fconst_0
       402: invokestatic  #273                // Method net/minecraft/class_5603.method_32091:(FFFFFF)Lnet/minecraft/class_5603;
       405: invokevirtual #230                // Method net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
       408: astore        9
       410: aload         8
       412: ldc_w         #290                // String Tongue_r2
       415: invokestatic  #215                // Method net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
       418: iconst_2
       419: iconst_2
       420: invokevirtual #238                // Method net/minecraft/class_5606.method_32101:(II)Lnet/minecraft/class_5606;
       423: ldc_w         #265                // float -0.25f
       426: ldc           #251                // float -2.0f
       428: fconst_0
       429: ldc           #216                // float 0.5f
       431: fconst_2
       432: fconst_0
       433: new           #243                // class net/minecraft/class_5605
       436: dup
       437: fconst_0
       438: invokespecial #246                // Method net/minecraft/class_5605."<init>":(F)V
       441: invokevirtual #250                // Method net/minecraft/class_5606.method_32098:(FFFFFFLnet/minecraft/class_5605;)Lnet/minecraft/class_5606;
       444: ldc_w         #285                // float -0.0136f
       447: fconst_0
       448: ldc_w         #286                // float -1.7121f
       451: ldc_w         #287                // float 1.5708f
       454: ldc_w         #269                // float 0.3491f
       457: fconst_0
       458: invokestatic  #273                // Method net/minecraft/class_5603.method_32091:(FFFFFF)Lnet/minecraft/class_5603;
       461: invokevirtual #230                // Method net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
       464: astore        10
       466: aload         8
       468: ldc_w         #292                // String Tongue_r3
       471: invokestatic  #215                // Method net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
       474: iconst_2
       475: iconst_2
       476: invokevirtual #238                // Method net/minecraft/class_5606.method_32101:(II)Lnet/minecraft/class_5606;
       479: fconst_0
       480: ldc           #255                // float -0.0833f
       482: ldc           #254                // float 0.9167f
       484: ldc           #216                // float 0.5f
       486: fconst_2
       487: fconst_0
       488: new           #243                // class net/minecraft/class_5605
       491: dup
       492: fconst_0
       493: invokespecial #246                // Method net/minecraft/class_5605."<init>":(F)V
       496: invokevirtual #250                // Method net/minecraft/class_5606.method_32098:(FFFFFFLnet/minecraft/class_5605;)Lnet/minecraft/class_5606;
       499: ldc_w         #293                // float -0.2636f
       502: ldc           #254                // float 0.9167f
       504: ldc_w         #294                // float -1.6288f
       507: ldc_w         #287                // float 1.5708f
       510: fconst_0
       511: fconst_0
       512: invokestatic  #273                // Method net/minecraft/class_5603.method_32091:(FFFFFF)Lnet/minecraft/class_5603;
       515: invokevirtual #230                // Method net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
       518: astore        11
       520: aload_2
       521: ldc_w         #296                // String Body
       524: invokestatic  #215                // Method net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
       527: fconst_0
       528: ldc_w         #297                // float 0.4583f
       531: ldc_w         #298                // float -17.5f
       534: invokestatic  #224                // Method net/minecraft/class_5603.method_32090:(FFF)Lnet/minecraft/class_5603;
       537: invokevirtual #230                // Method net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
       540: astore        12
       542: aload         12
       544: ldc_w         #300                // String Neck1
       547: invokestatic  #215                // Method net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
       550: bipush        28
       552: bipush        27
       554: invokevirtual #238                // Method net/minecraft/class_5606.method_32101:(II)Lnet/minecraft/class_5606;
       557: ldc_w         #301                // float -1.0f
       560: fconst_0
       561: ldc_w         #260                // float -3.0f
       564: fconst_2
       565: fconst_2
       566: ldc           #241                // float 3.0f
       568: new           #243                // class net/minecraft/class_5605
       571: dup
       572: fconst_0
       573: invokespecial #246                // Method net/minecraft/class_5605."<init>":(F)V
       576: invokevirtual #250                // Method net/minecraft/class_5606.method_32098:(FFFFFFLnet/minecraft/class_5605;)Lnet/minecraft/class_5606;
       579: fconst_0
       580: ldc_w         #301                // float -1.0f
       583: ldc           #241                // float 3.0f
       585: invokestatic  #224                // Method net/minecraft/class_5603.method_32090:(FFF)Lnet/minecraft/class_5603;
       588: invokevirtual #230                // Method net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
       591: astore        13
       593: aload         12
       595: ldc_w         #303                // String Neck3
       598: invokestatic  #215                // Method net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
       601: fconst_0
       602: fconst_1
       603: ldc           #241                // float 3.0f
       605: invokestatic  #224                // Method net/minecraft/class_5603.method_32090:(FFF)Lnet/minecraft/class_5603;
       608: invokevirtual #230                // Method net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
       611: astore        14
       613: aload         14
       615: ldc_w         #305                // String Neck2
       618: invokestatic  #215                // Method net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
       621: iconst_0
       622: bipush        30
       624: invokevirtual #238                // Method net/minecraft/class_5606.method_32101:(II)Lnet/minecraft/class_5606;
       627: ldc_w         #301                // float -1.0f
       630: ldc_w         #301                // float -1.0f
       633: ldc           #239                // float -1.5f
       635: fconst_2
       636: fconst_2
       637: ldc           #241                // float 3.0f
       639: new           #243                // class net/minecraft/class_5605
       642: dup
       643: fconst_0
       644: invokespecial #246                // Method net/minecraft/class_5605."<init>":(F)V
       647: invokevirtual #250                // Method net/minecraft/class_5606.method_32098:(FFFFFFLnet/minecraft/class_5605;)Lnet/minecraft/class_5606;
       650: fconst_0
       651: ldc_w         #301                // float -1.0f
       654: ldc_w         #306                // float 1.5f
       657: invokestatic  #224                // Method net/minecraft/class_5603.method_32090:(FFF)Lnet/minecraft/class_5603;
       660: invokevirtual #230                // Method net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
       663: astore        15
       665: aload         14
       667: ldc_w         #308                // String Body6
       670: invokestatic  #215                // Method net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
       673: fconst_0
       674: fconst_0
       675: ldc           #241                // float 3.0f
       677: invokestatic  #224                // Method net/minecraft/class_5603.method_32090:(FFF)Lnet/minecraft/class_5603;
       680: invokevirtual #230                // Method net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
       683: astore        16
       685: aload         16
       687: ldc_w         #310                // String Body1
       690: invokestatic  #215                // Method net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
       693: bipush        18
       695: iconst_0
       696: invokevirtual #238                // Method net/minecraft/class_5606.method_32101:(II)Lnet/minecraft/class_5606;
       699: ldc           #239                // float -1.5f
       701: ldc           #218                // float -2.5f
       703: ldc_w         #301                // float -1.0f
       706: fconst_2
       707: ldc           #175                // float 2.5f
       709: ldc_w         #311                // float 6.0f
       712: new           #243                // class net/minecraft/class_5605
       715: dup
       716: fconst_0
       717: invokespecial #246                // Method net/minecraft/class_5605."<init>":(F)V
       720: invokevirtual #250                // Method net/minecraft/class_5606.method_32098:(FFFFFFLnet/minecraft/class_5605;)Lnet/minecraft/class_5606;
       723: ldc           #216                // float 0.5f
       725: fconst_0
       726: fconst_1
       727: invokestatic  #224                // Method net/minecraft/class_5603.method_32090:(FFF)Lnet/minecraft/class_5603;
       730: invokevirtual #230                // Method net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
       733: astore        17
       735: aload         16
       737: ldc_w         #313                // String Body7
       740: invokestatic  #215                // Method net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
       743: fconst_0
       744: fconst_0
       745: ldc_w         #311                // float 6.0f
       748: invokestatic  #224                // Method net/minecraft/class_5603.method_32090:(FFF)Lnet/minecraft/class_5603;
       751: invokevirtual #230                // Method net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
       754: astore        18
       756: aload         18
       758: ldc_w         #315                // String Body2
       761: invokestatic  #215                // Method net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
       764: iconst_0
       765: iconst_0
       766: invokevirtual #238                // Method net/minecraft/class_5606.method_32101:(II)Lnet/minecraft/class_5606;
       769: ldc           #239                // float -1.5f
       771: ldc_w         #276                // float -1.25f
       774: ldc_w         #316                // float -6.0f
       777: ldc           #241                // float 3.0f
       779: ldc           #175                // float 2.5f
       781: ldc_w         #311                // float 6.0f
       784: new           #243                // class net/minecraft/class_5605
       787: dup
       788: fconst_0
       789: invokespecial #246                // Method net/minecraft/class_5605."<init>":(F)V
       792: invokevirtual #250                // Method net/minecraft/class_5606.method_32098:(FFFFFFLnet/minecraft/class_5605;)Lnet/minecraft/class_5606;
       795: fconst_0
       796: ldc_w         #276                // float -1.25f
       799: ldc_w         #311                // float 6.0f
       802: invokestatic  #224                // Method net/minecraft/class_5603.method_32090:(FFF)Lnet/minecraft/class_5603;
       805: invokevirtual #230                // Method net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
       808: astore        19
       810: aload         18
       812: ldc_w         #318                // String Body8
       815: invokestatic  #215                // Method net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
       818: fconst_0
       819: fconst_0
       820: ldc_w         #311                // float 6.0f
       823: invokestatic  #224                // Method net/minecraft/class_5603.method_32090:(FFF)Lnet/minecraft/class_5603;
       826: invokevirtual #230                // Method net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
       829: astore        20
       831: aload         20
       833: ldc_w         #320                // String Body3
       836: invokestatic  #215                // Method net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
       839: iconst_0
       840: bipush        9
       842: invokevirtual #238                // Method net/minecraft/class_5606.method_32101:(II)Lnet/minecraft/class_5606;
       845: ldc_w         #321                // float -0.6477f
       848: ldc_w         #276                // float -1.25f
       851: ldc_w         #322                // float -3.2081f
       854: ldc           #241                // float 3.0f
       856: ldc           #175                // float 2.5f
       858: ldc_w         #311                // float 6.0f
       861: new           #243                // class net/minecraft/class_5605
       864: dup
       865: fconst_0
       866: invokespecial #246                // Method net/minecraft/class_5605."<init>":(F)V
       869: invokevirtual #250                // Method net/minecraft/class_5606.method_32098:(FFFFFFLnet/minecraft/class_5605;)Lnet/minecraft/class_5606;
       872: ldc_w         #323                // float -0.8523f
       875: ldc_w         #276                // float -1.25f
       878: ldc_w         #324                // float 3.2081f
       881: invokestatic  #224                // Method net/minecraft/class_5603.method_32090:(FFF)Lnet/minecraft/class_5603;
       884: invokevirtual #230                // Method net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
       887: astore        21
       889: aload         20
       891: ldc_w         #326                // String Body9
       894: invokestatic  #215                // Method net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
       897: fconst_0
       898: fconst_0
       899: ldc_w         #311                // float 6.0f
       902: invokestatic  #224                // Method net/minecraft/class_5603.method_32090:(FFF)Lnet/minecraft/class_5603;
       905: invokevirtual #230                // Method net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
       908: astore        22
       910: aload         22
       912: ldc_w         #328                // String Body4
       915: invokestatic  #215                // Method net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
       918: iconst_0
       919: bipush        18
       921: invokevirtual #238                // Method net/minecraft/class_5606.method_32101:(II)Lnet/minecraft/class_5606;
       924: ldc           #251                // float -2.0f
       926: ldc           #251                // float -2.0f
       928: ldc_w         #301                // float -1.0f
       931: ldc           #241                // float 3.0f
       933: fconst_2
       934: ldc_w         #311                // float 6.0f
       937: new           #243                // class net/minecraft/class_5605
       940: dup
       941: fconst_0
       942: invokespecial #246                // Method net/minecraft/class_5605."<init>":(F)V
       945: invokevirtual #250                // Method net/minecraft/class_5606.method_32098:(FFFFFFLnet/minecraft/class_5605;)Lnet/minecraft/class_5606;
       948: ldc           #216                // float 0.5f
       950: fconst_0
       951: fconst_1
       952: invokestatic  #224                // Method net/minecraft/class_5603.method_32090:(FFF)Lnet/minecraft/class_5603;
       955: invokevirtual #230                // Method net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
       958: astore        23
       960: aload         22
       962: ldc_w         #330                // String Body10
       965: invokestatic  #215                // Method net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
       968: fconst_0
       969: fconst_0
       970: ldc_w         #311                // float 6.0f
       973: invokestatic  #224                // Method net/minecraft/class_5603.method_32090:(FFF)Lnet/minecraft/class_5603;
       976: invokevirtual #230                // Method net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
       979: astore        24
       981: aload         24
       983: ldc_w         #332                // String Body5
       986: invokestatic  #215                // Method net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
       989: bipush        18
       991: bipush        21
       993: invokevirtual #238                // Method net/minecraft/class_5606.method_32101:(II)Lnet/minecraft/class_5606;
       996: ldc           #239                // float -1.5f
       998: ldc           #251                // float -2.0f
      1000: ldc_w         #301                // float -1.0f
      1003: fconst_2
      1004: fconst_2
      1005: ldc           #253                // float 4.0f
      1007: new           #243                // class net/minecraft/class_5605
      1010: dup
      1011: fconst_0
      1012: invokespecial #246                // Method net/minecraft/class_5605."<init>":(F)V
      1015: invokevirtual #250                // Method net/minecraft/class_5606.method_32098:(FFFFFFLnet/minecraft/class_5605;)Lnet/minecraft/class_5606;
      1018: ldc           #216                // float 0.5f
      1020: fconst_0
      1021: fconst_1
      1022: invokestatic  #224                // Method net/minecraft/class_5603.method_32090:(FFF)Lnet/minecraft/class_5603;
      1025: invokevirtual #230                // Method net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
      1028: astore        25
      1030: aload         24
      1032: ldc_w         #334                // String Tail4
      1035: invokestatic  #215                // Method net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
      1038: fconst_0
      1039: fconst_0
      1040: ldc           #253                // float 4.0f
      1042: invokestatic  #224                // Method net/minecraft/class_5603.method_32090:(FFF)Lnet/minecraft/class_5603;
      1045: invokevirtual #230                // Method net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
      1048: astore        26
      1050: aload         26
      1052: ldc_w         #336                // String Tail1
      1055: invokestatic  #215                // Method net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
      1058: bipush        18
      1060: bipush        9
      1062: invokevirtual #238                // Method net/minecraft/class_5606.method_32101:(II)Lnet/minecraft/class_5606;
      1065: ldc_w         #301                // float -1.0f
      1068: ldc           #251                // float -2.0f
      1070: ldc_w         #260                // float -3.0f
      1073: fconst_2
      1074: fconst_2
      1075: ldc           #253                // float 4.0f
      1077: new           #243                // class net/minecraft/class_5605
      1080: dup
      1081: fconst_0
      1082: invokespecial #246                // Method net/minecraft/class_5605."<init>":(F)V
      1085: invokevirtual #250                // Method net/minecraft/class_5606.method_32098:(FFFFFFLnet/minecraft/class_5605;)Lnet/minecraft/class_5606;
      1088: fconst_0
      1089: fconst_0
      1090: ldc           #241                // float 3.0f
      1092: invokestatic  #224                // Method net/minecraft/class_5603.method_32090:(FFF)Lnet/minecraft/class_5603;
      1095: invokevirtual #230                // Method net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
      1098: astore        27
      1100: aload         26
      1102: ldc_w         #338                // String Tail5
      1105: invokestatic  #215                // Method net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
      1108: fconst_0
      1109: fconst_0
      1110: ldc           #253                // float 4.0f
      1112: invokestatic  #224                // Method net/minecraft/class_5603.method_32090:(FFF)Lnet/minecraft/class_5603;
      1115: invokevirtual #230                // Method net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
      1118: astore        28
      1120: aload         28
      1122: ldc_w         #340                // String Tail2
      1125: invokestatic  #215                // Method net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
      1128: bipush        18
      1130: bipush        15
      1132: invokevirtual #238                // Method net/minecraft/class_5606.method_32101:(II)Lnet/minecraft/class_5606;
      1135: ldc_w         #301                // float -1.0f
      1138: ldc           #251                // float -2.0f
      1140: ldc           #251                // float -2.0f
      1142: fconst_2
      1143: fconst_2
      1144: ldc           #253                // float 4.0f
      1146: new           #243                // class net/minecraft/class_5605
      1149: dup
      1150: fconst_0
      1151: invokespecial #246                // Method net/minecraft/class_5605."<init>":(F)V
      1154: invokevirtual #250                // Method net/minecraft/class_5606.method_32098:(FFFFFFLnet/minecraft/class_5605;)Lnet/minecraft/class_5606;
      1157: fconst_0
      1158: fconst_0
      1159: fconst_2
      1160: invokestatic  #224                // Method net/minecraft/class_5603.method_32090:(FFF)Lnet/minecraft/class_5603;
      1163: invokevirtual #230                // Method net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
      1166: astore        29
      1168: aload         28
      1170: ldc_w         #342                // String Tail6
      1173: invokestatic  #215                // Method net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
      1176: fconst_0
      1177: fconst_0
      1178: ldc           #253                // float 4.0f
      1180: invokestatic  #224                // Method net/minecraft/class_5603.method_32090:(FFF)Lnet/minecraft/class_5603;
      1183: invokevirtual #230                // Method net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
      1186: astore        30
      1188: aload         30
      1190: ldc_w         #344                // String Tail3
      1193: invokestatic  #215                // Method net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
      1196: bipush        30
      1198: bipush        14
      1200: invokevirtual #238                // Method net/minecraft/class_5606.method_32101:(II)Lnet/minecraft/class_5606;
      1203: fconst_0
      1204: ldc           #239                // float -1.5f
      1206: ldc_w         #301                // float -1.0f
      1209: fconst_1
      1210: ldc_w         #306                // float 1.5f
      1213: ldc           #241                // float 3.0f
      1215: new           #243                // class net/minecraft/class_5605
      1218: dup
      1219: fconst_0
      1220: invokespecial #246                // Method net/minecraft/class_5605."<init>":(F)V
      1223: invokevirtual #250                // Method net/minecraft/class_5606.method_32098:(FFFFFFLnet/minecraft/class_5605;)Lnet/minecraft/class_5606;
      1226: ldc_w         #266                // float -0.5f
      1229: fconst_0
      1230: fconst_1
      1231: invokestatic  #224                // Method net/minecraft/class_5603.method_32090:(FFF)Lnet/minecraft/class_5603;
      1234: invokevirtual #230                // Method net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
      1237: astore        31
      1239: aload         30
      1241: ldc_w         #346                // String Rattle
      1244: invokestatic  #215                // Method net/minecraft/class_5606.method_32108:()Lnet/minecraft/class_5606;
      1247: bipush        31
      1249: bipush        9
      1251: invokevirtual #238                // Method net/minecraft/class_5606.method_32101:(II)Lnet/minecraft/class_5606;
      1254: ldc_w         #266                // float -0.5f
      1257: ldc_w         #347                // float -0.75f
      1260: fconst_0
      1261: fconst_1
      1262: ldc_w         #306                // float 1.5f
      1265: ldc           #241                // float 3.0f
      1267: new           #243                // class net/minecraft/class_5605
      1270: dup
      1271: fconst_0
      1272: invokespecial #246                // Method net/minecraft/class_5605."<init>":(F)V
      1275: invokevirtual #250                // Method net/minecraft/class_5606.method_32098:(FFFFFFLnet/minecraft/class_5605;)Lnet/minecraft/class_5606;
      1278: fconst_0
      1279: ldc_w         #347                // float -0.75f
      1282: ldc           #241                // float 3.0f
      1284: invokestatic  #224                // Method net/minecraft/class_5603.method_32090:(FFF)Lnet/minecraft/class_5603;
      1287: invokevirtual #230                // Method net/minecraft/class_5610.method_32117:(Ljava/lang/String;Lnet/minecraft/class_5606;Lnet/minecraft/class_5603;)Lnet/minecraft/class_5610;
      1290: astore        32
      1292: aload_0
      1293: bipush        64
      1295: bipush        64
      1297: invokestatic  #353                // Method net/minecraft/class_5607.method_32110:(Lnet/minecraft/class_5609;II)Lnet/minecraft/class_5607;
      1300: areturn
      LineNumberTable:
        line 84: 0
        line 85: 8
        line 86: 13
        line 88: 32
        line 90: 50
        line 91: 86
        line 90: 118
        line 93: 123
        line 94: 164
        line 93: 201
        line 96: 206
        line 98: 268
        line 100: 330
        line 102: 354
        line 104: 410
        line 106: 466
        line 108: 520
        line 110: 542
        line 112: 593
        line 114: 613
        line 116: 665
        line 118: 685
        line 120: 735
        line 122: 756
        line 124: 810
        line 126: 831
        line 128: 889
        line 130: 910
        line 132: 960
        line 134: 981
        line 136: 1030
        line 138: 1050
        line 140: 1100
        line 142: 1120
        line 144: 1168
        line 146: 1188
        line 148: 1239
        line 149: 1292
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            8    1293     0 modelData   Lnet/minecraft/class_5609;
           13    1288     1 modelPartData   Lnet/minecraft/class_5610;
           32    1269     2 RattleSnake   Lnet/minecraft/class_5610;
           50    1251     3  Head   Lnet/minecraft/class_5610;
          123    1178     4 Face22   Lnet/minecraft/class_5610;
          206    1095     5 Face3   Lnet/minecraft/class_5610;
          268    1033     6 eys_r1   Lnet/minecraft/class_5610;
          330     971     7 eys_r2   Lnet/minecraft/class_5610;
          354     947     8 Tongue   Lnet/minecraft/class_5610;
          410     891     9 Tongue_r1   Lnet/minecraft/class_5610;
          466     835    10 Tongue_r2   Lnet/minecraft/class_5610;
          520     781    11 Tongue_r3   Lnet/minecraft/class_5610;
          542     759    12  Body   Lnet/minecraft/class_5610;
          593     708    13 Neck1   Lnet/minecraft/class_5610;
          613     688    14 Neck3   Lnet/minecraft/class_5610;
          665     636    15 Neck2   Lnet/minecraft/class_5610;
          685     616    16 Body6   Lnet/minecraft/class_5610;
          735     566    17 Body1   Lnet/minecraft/class_5610;
          756     545    18 Body7   Lnet/minecraft/class_5610;
          810     491    19 Body2   Lnet/minecraft/class_5610;
          831     470    20 Body8   Lnet/minecraft/class_5610;
          889     412    21 Body3   Lnet/minecraft/class_5610;
          910     391    22 Body9   Lnet/minecraft/class_5610;
          960     341    23 Body4   Lnet/minecraft/class_5610;
          981     320    24 Body10   Lnet/minecraft/class_5610;
         1030     271    25 Body5   Lnet/minecraft/class_5610;
         1050     251    26 Tail4   Lnet/minecraft/class_5610;
         1100     201    27 Tail1   Lnet/minecraft/class_5610;
         1120     181    28 Tail5   Lnet/minecraft/class_5610;
         1168     133    29 Tail2   Lnet/minecraft/class_5610;
         1188     113    30 Tail6   Lnet/minecraft/class_5610;
         1239      62    31 Tail3   Lnet/minecraft/class_5610;
         1292       9    32 Rattle   Lnet/minecraft/class_5610;

  public void method_2819(java.lang.Object);
    descriptor: (Ljava/lang/Object;)V
    flags: (0x1041) ACC_PUBLIC, ACC_BRIDGE, ACC_SYNTHETIC
    Code:
      stack=2, locals=2, args_size=2
         0: aload_0
         1: aload_1
         2: checkcast     #104                // class net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState
         5: invokevirtual #360                // Method setAngles:(Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;)V
         8: return
      LineNumberTable:
        line 14: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       9     0  this   Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeModel;
    MethodParameters:
      Name                           Flags
      <no name>                      synthetic

  static {};
    descriptor: ()V
    flags: (0x0008) ACC_STATIC
    Code:
      stack=4, locals=0, args_size=0
         0: new           #363                // class net/minecraft/class_5601
         3: dup
         4: ldc_w         #365                // String cubeanimals
         7: ldc_w         #367                // String rattlesnake
        10: invokestatic  #373                // Method net/minecraft/class_2960.method_60655:(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/class_2960;
        13: ldc_w         #375                // String main
        16: invokespecial #378                // Method net/minecraft/class_5601."<init>":(Lnet/minecraft/class_2960;Ljava/lang/String;)V
        19: putstatic     #380                // Field LAYER:Lnet/minecraft/class_5601;
        22: return
      LineNumberTable:
        line 16: 0
        line 18: 10
        line 16: 22
}
InnerClasses:
  public static final #11= #8 of #10;     // Lookup=class java/lang/invoke/MethodHandles$Lookup of class java/lang/invoke/MethodHandles
Signature: #3                           // Lnet/minecraft/class_583<Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeRenderState;>;
SourceFile: "RattleSnakeModel.java"
BootstrapMethods:
  0: #92 REF_invokeStatic java/lang/invoke/LambdaMetafactory.metafactory:(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
    Method arguments:
      #79 (Ljava/lang/Object;)V
      #84 REF_invokeVirtual net/minecraft/class_630.method_41923:()V
      #85 (Lnet/minecraft/class_630;)V

### net.suprk.ufauna.entity.client.RattleSnake.RattleSnakeAnimations
Classfile jar:file:///tmp/cc146/nested/cubeanimals.jar!/net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeAnimations.class
  Last modified Sep 25, 2026; size 12018 bytes
  SHA-256 checksum ba86ed3c5bdbf0c48abde3b7b3b1210a8dffc7187fdddbf311ee038a34803273
  Compiled from "RattleSnakeAnimations.java"
public class net.suprk.ufauna.entity.client.RattleSnake.RattleSnakeAnimations
  minor version: 0
  major version: 65
  flags: (0x0021) ACC_PUBLIC, ACC_SUPER
  this_class: #2                          // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeAnimations
  super_class: #4                         // java/lang/Object
  interfaces: 0, fields: 6, methods: 2, attributes: 2
Constant pool:
    #1 = Utf8               net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeAnimations
    #2 = Class              #1            // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeAnimations
    #3 = Utf8               java/lang/Object
    #4 = Class              #3            // java/lang/Object
    #5 = Utf8               RattleSnakeAnimations.java
    #6 = Utf8               net/minecraft/class_7184$class_7185
    #7 = Class              #6            // net/minecraft/class_7184$class_7185
    #8 = Utf8               net/minecraft/class_7184
    #9 = Class              #8            // net/minecraft/class_7184
   #10 = Utf8               class_7185
   #11 = Utf8               net/minecraft/class_7179$class_7183
   #12 = Class              #11           // net/minecraft/class_7179$class_7183
   #13 = Utf8               net/minecraft/class_7179
   #14 = Class              #13           // net/minecraft/class_7179
   #15 = Utf8               class_7183
   #16 = Utf8               net/minecraft/class_7179$class_7182
   #17 = Class              #16           // net/minecraft/class_7179$class_7182
   #18 = Utf8               class_7182
   #19 = Utf8               net/minecraft/class_7179$class_7181
   #20 = Class              #19           // net/minecraft/class_7179$class_7181
   #21 = Utf8               class_7181
   #22 = Utf8               net/minecraft/class_7179$class_7180
   #23 = Class              #22           // net/minecraft/class_7179$class_7180
   #24 = Utf8               class_7180
   #25 = Utf8               ANIM_RATTLESNAKE_WALK2
   #26 = Utf8               Lnet/minecraft/class_7184;
   #27 = Utf8               ANIM_RATTLESNAKE_WALK
   #28 = Utf8               ANIM_RATTLESNAKE_STAND
   #29 = Utf8               ANIM_RATTLESNAKE_COILED_UP2
   #30 = Utf8               ANIM_RATTLESNAKE_COILED_UP
   #31 = Utf8               ANIM_RATTLESNAKE_ATTACK
   #32 = Utf8               <init>
   #33 = Utf8               ()V
   #34 = NameAndType        #32:#33       // "<init>":()V
   #35 = Methodref          #4.#34        // java/lang/Object."<init>":()V
   #36 = Utf8               this
   #37 = Utf8               Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeAnimations;
   #38 = Utf8               <clinit>
   #39 = Float              3.0f
   #40 = Utf8               method_41818
   #41 = Utf8               (F)Lnet/minecraft/class_7184$class_7185;
   #42 = NameAndType        #40:#41       // method_41818:(F)Lnet/minecraft/class_7184$class_7185;
   #43 = Methodref          #7.#42        // net/minecraft/class_7184$class_7185.method_41818:(F)Lnet/minecraft/class_7184$class_7185;
   #44 = Utf8               method_41817
   #45 = Utf8               ()Lnet/minecraft/class_7184$class_7185;
   #46 = NameAndType        #44:#45       // method_41817:()Lnet/minecraft/class_7184$class_7185;
   #47 = Methodref          #7.#46        // net/minecraft/class_7184$class_7185.method_41817:()Lnet/minecraft/class_7184$class_7185;
   #48 = Utf8               RattleSnake
   #49 = String             #48           // RattleSnake
   #50 = Utf8               field_37887
   #51 = Utf8               Lnet/minecraft/class_7179$class_7182;
   #52 = NameAndType        #50:#51       // field_37887:Lnet/minecraft/class_7179$class_7182;
   #53 = Fieldref           #12.#52       // net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
   #54 = Utf8               net/minecraft/class_7186
   #55 = Class              #54           // net/minecraft/class_7186
   #56 = Float              -20.0f
   #57 = Utf8               net/minecraft/class_7187
   #58 = Class              #57           // net/minecraft/class_7187
   #59 = Utf8               method_41829
   #60 = Utf8               (FFF)Lorg/joml/Vector3f;
   #61 = NameAndType        #59:#60       // method_41829:(FFF)Lorg/joml/Vector3f;
   #62 = Methodref          #58.#61       // net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
   #63 = Utf8               field_37885
   #64 = Utf8               Lnet/minecraft/class_7179$class_7180;
   #65 = NameAndType        #63:#64       // field_37885:Lnet/minecraft/class_7179$class_7180;
   #66 = Fieldref           #20.#65       // net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
   #67 = Utf8               (FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
   #68 = NameAndType        #32:#67       // "<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
   #69 = Methodref          #55.#68       // net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
   #70 = Float              1.5f
   #71 = Float              20.0f
   #72 = Float              2.9583f
   #73 = Utf8               (Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
   #74 = NameAndType        #32:#73       // "<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
   #75 = Methodref          #14.#74       // net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
   #76 = Utf8               method_41820
   #77 = Utf8               (Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
   #78 = NameAndType        #76:#77       // method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
   #79 = Methodref          #7.#78        // net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
   #80 = Utf8               Head
   #81 = String             #80           // Head
   #82 = Float              -10.0f
   #83 = Utf8               field_37884
   #84 = NameAndType        #83:#64       // field_37884:Lnet/minecraft/class_7179$class_7180;
   #85 = Fieldref           #20.#84       // net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
   #86 = Utf8               field_37886
   #87 = NameAndType        #86:#51       // field_37886:Lnet/minecraft/class_7179$class_7182;
   #88 = Fieldref           #12.#87       // net/minecraft/class_7179$class_7183.field_37886:Lnet/minecraft/class_7179$class_7182;
   #89 = Utf8               method_41823
   #90 = NameAndType        #89:#60       // method_41823:(FFF)Lorg/joml/Vector3f;
   #91 = Methodref          #58.#90       // net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
   #92 = Utf8               Neck1
   #93 = String             #92           // Neck1
   #94 = Float              0.5f
   #95 = Utf8               Rattle
   #96 = String             #95           // Rattle
   #97 = Float              47.5f
   #98 = Utf8               Neck3
   #99 = String             #98           // Neck3
  #100 = Float              -30.0f
  #101 = Float              0.75f
  #102 = Float              2.5f
  #103 = Float              30.0f
  #104 = Float              2.25f
  #105 = Float              -2.5f
  #106 = Utf8               Body6
  #107 = String             #106          // Body6
  #108 = Float              -35.0f
  #109 = Float              27.5f
  #110 = Float              35.0f
  #111 = Float              -27.5f
  #112 = Utf8               Body7
  #113 = String             #112          // Body7
  #114 = Float              57.5f
  #115 = Float              10.0f
  #116 = Float              -57.5f
  #117 = Float              5.0f
  #118 = Utf8               Body8
  #119 = String             #118          // Body8
  #120 = Float              65.0f
  #121 = Float              -25.0f
  #122 = Float              -65.0f
  #123 = Float              25.0f
  #124 = Utf8               Body9
  #125 = String             #124          // Body9
  #126 = Float              22.5f
  #127 = Float              -42.5f
  #128 = Float              -22.5f
  #129 = Float              42.5f
  #130 = Utf8               Body10
  #131 = String             #130          // Body10
  #132 = Utf8               Tail4
  #133 = String             #132          // Tail4
  #134 = Float              -40.0f
  #135 = Float              37.5f
  #136 = Utf8               Tail5
  #137 = String             #136          // Tail5
  #138 = Float              -50.0f
  #139 = Float              50.0f
  #140 = Float              -45.0f
  #141 = Utf8               Tail6
  #142 = String             #141          // Tail6
  #143 = Float              25.8915f
  #144 = Float              48.6554f
  #145 = Float              18.2499f
  #146 = Float              -8.1809f
  #147 = Float              19.835f
  #148 = Float              -31.775f
  #149 = Float              18.9284f
  #150 = Float              -13.0684f
  #151 = Float              25.8804f
  #152 = Float              45.9705f
  #153 = Utf8               method_41821
  #154 = Utf8               ()Lnet/minecraft/class_7184;
  #155 = NameAndType        #153:#154     // method_41821:()Lnet/minecraft/class_7184;
  #156 = Methodref          #7.#155       // net/minecraft/class_7184$class_7185.method_41821:()Lnet/minecraft/class_7184;
  #157 = NameAndType        #25:#26       // ANIM_RATTLESNAKE_WALK2:Lnet/minecraft/class_7184;
  #158 = Fieldref           #2.#157       // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeAnimations.ANIM_RATTLESNAKE_WALK2:Lnet/minecraft/class_7184;
  #159 = Float              -5.0f
  #160 = Utf8               Body
  #161 = String             #160          // Body
  #162 = Float              -17.5f
  #163 = Float              0.25f
  #164 = Float              17.5f
  #165 = Float              40.0f
  #166 = Float              70.0f
  #167 = Float              -70.0f
  #168 = Float              -47.5f
  #169 = Float              15.0f
  #170 = NameAndType        #27:#26       // ANIM_RATTLESNAKE_WALK:Lnet/minecraft/class_7184;
  #171 = Fieldref           #2.#170       // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeAnimations.ANIM_RATTLESNAKE_WALK:Lnet/minecraft/class_7184;
  #172 = Float              0.0417f
  #173 = Float              9.75f
  #174 = Float              60.0f
  #175 = Float              90.0f
  #176 = Float              -90.0f
  #177 = Float              15.7664f
  #178 = Float              49.5098f
  #179 = Float              0.2089f
  #180 = NameAndType        #28:#26       // ANIM_RATTLESNAKE_STAND:Lnet/minecraft/class_7184;
  #181 = Fieldref           #2.#180       // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeAnimations.ANIM_RATTLESNAKE_STAND:Lnet/minecraft/class_7184;
  #182 = Float              5.5f
  #183 = Float              6.75f
  #184 = Float              18.25f
  #185 = Float              7.0f
  #186 = Float              8.0f
  #187 = Utf8               Neck2
  #188 = String             #187          // Neck2
  #189 = Float              -6.2607f
  #190 = Float              -42.4066f
  #191 = Float              2.3137f
  #192 = Float              6.5f
  #193 = Utf8               Body1
  #194 = String             #193          // Body1
  #195 = Float              155.0f
  #196 = Float              -180.0f
  #197 = Float              4.0f
  #198 = Float              6.0f
  #199 = Float              16.0f
  #200 = Utf8               Body2
  #201 = String             #200          // Body2
  #202 = Float              116.2474f
  #203 = Float              -63.9688f
  #204 = Float              -153.7998f
  #205 = Float              -1.0f
  #206 = Utf8               Body3
  #207 = String             #206          // Body3
  #208 = Utf8               Body4
  #209 = String             #208          // Body4
  #210 = Float              31.0686f
  #211 = Float              29.9119f
  #212 = Float              -23.7553f
  #213 = Float              -15.0f
  #214 = Float              72.5f
  #215 = Float              35.9802f
  #216 = Float              6.1147f
  #217 = Float              -16.0205f
  #218 = Utf8               Face3
  #219 = String             #218          // Face3
  #220 = Utf8               Face22
  #221 = String             #220          // Face22
  #222 = Utf8               Tongue
  #223 = String             #222          // Tongue
  #224 = NameAndType        #29:#26       // ANIM_RATTLESNAKE_COILED_UP2:Lnet/minecraft/class_7184;
  #225 = Fieldref           #2.#224       // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeAnimations.ANIM_RATTLESNAKE_COILED_UP2:Lnet/minecraft/class_7184;
  #226 = Float              -4.0f
  #227 = Float              -1.5f
  #228 = NameAndType        #30:#26       // ANIM_RATTLESNAKE_COILED_UP:Lnet/minecraft/class_7184;
  #229 = Fieldref           #2.#228       // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeAnimations.ANIM_RATTLESNAKE_COILED_UP:Lnet/minecraft/class_7184;
  #230 = Float              0.0833f
  #231 = Float              7.25f
  #232 = Float              -1.75f
  #233 = Float              -12.5f
  #234 = NameAndType        #31:#26       // ANIM_RATTLESNAKE_ATTACK:Lnet/minecraft/class_7184;
  #235 = Fieldref           #2.#234       // net/suprk/ufauna/entity/client/RattleSnake/RattleSnakeAnimations.ANIM_RATTLESNAKE_ATTACK:Lnet/minecraft/class_7184;
  #236 = Utf8               Code
  #237 = Utf8               LineNumberTable
  #238 = Utf8               LocalVariableTable
  #239 = Utf8               InnerClasses
  #240 = Utf8               SourceFile
{
  public static final net.minecraft.class_7184 ANIM_RATTLESNAKE_WALK2;
    descriptor: Lnet/minecraft/class_7184;
    flags: (0x0019) ACC_PUBLIC, ACC_STATIC, ACC_FINAL

  public static final net.minecraft.class_7184 ANIM_RATTLESNAKE_WALK;
    descriptor: Lnet/minecraft/class_7184;
    flags: (0x0019) ACC_PUBLIC, ACC_STATIC, ACC_FINAL

  public static final net.minecraft.class_7184 ANIM_RATTLESNAKE_STAND;
    descriptor: Lnet/minecraft/class_7184;
    flags: (0x0019) ACC_PUBLIC, ACC_STATIC, ACC_FINAL

  public static final net.minecraft.class_7184 ANIM_RATTLESNAKE_COILED_UP2;
    descriptor: Lnet/minecraft/class_7184;
    flags: (0x0019) ACC_PUBLIC, ACC_STATIC, ACC_FINAL

  public static final net.minecraft.class_7184 ANIM_RATTLESNAKE_COILED_UP;
    descriptor: Lnet/minecraft/class_7184;
    flags: (0x0019) ACC_PUBLIC, ACC_STATIC, ACC_FINAL

  public static final net.minecraft.class_7184 ANIM_RATTLESNAKE_ATTACK;
    descriptor: Lnet/minecraft/class_7184;
    flags: (0x0019) ACC_PUBLIC, ACC_STATIC, ACC_FINAL

  public net.suprk.ufauna.entity.client.RattleSnake.RattleSnakeAnimations();
    descriptor: ()V
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #35                 // Method java/lang/Object."<init>":()V
         4: return
      LineNumberTable:
        line 8: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       5     0  this   Lnet/suprk/ufauna/entity/client/RattleSnake/RattleSnakeAnimations;

  static {};
    descriptor: ()V
    flags: (0x0008) ACC_STATIC
    Code:
      stack=14, locals=0, args_size=0
         0: ldc           #39                 // float 3.0f
         2: invokestatic  #43                 // Method net/minecraft/class_7184$class_7185.method_41818:(F)Lnet/minecraft/class_7184$class_7185;
         5: invokevirtual #47                 // Method net/minecraft/class_7184$class_7185.method_41817:()Lnet/minecraft/class_7184$class_7185;
         8: ldc           #49                 // String RattleSnake
        10: new           #14                 // class net/minecraft/class_7179
        13: dup
        14: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
        17: iconst_3
        18: anewarray     #55                 // class net/minecraft/class_7186
        21: dup
        22: iconst_0
        23: new           #55                 // class net/minecraft/class_7186
        26: dup
        27: fconst_0
        28: fconst_0
        29: ldc           #56                 // float -20.0f
        31: fconst_0
        32: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
        35: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
        38: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
        41: aastore
        42: dup
        43: iconst_1
        44: new           #55                 // class net/minecraft/class_7186
        47: dup
        48: ldc           #70                 // float 1.5f
        50: fconst_0
        51: ldc           #71                 // float 20.0f
        53: fconst_0
        54: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
        57: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
        60: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
        63: aastore
        64: dup
        65: iconst_2
        66: new           #55                 // class net/minecraft/class_7186
        69: dup
        70: ldc           #72                 // float 2.9583f
        72: fconst_0
        73: ldc           #56                 // float -20.0f
        75: fconst_0
        76: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
        79: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
        82: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
        85: aastore
        86: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
        89: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
        92: ldc           #81                 // String Head
        94: new           #14                 // class net/minecraft/class_7179
        97: dup
        98: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
       101: iconst_1
       102: anewarray     #55                 // class net/minecraft/class_7186
       105: dup
       106: iconst_0
       107: new           #55                 // class net/minecraft/class_7186
       110: dup
       111: fconst_0
       112: ldc           #82                 // float -10.0f
       114: fconst_0
       115: fconst_0
       116: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
       119: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
       122: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       125: aastore
       126: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
       129: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
       132: ldc           #81                 // String Head
       134: new           #14                 // class net/minecraft/class_7179
       137: dup
       138: getstatic     #88                 // Field net/minecraft/class_7179$class_7183.field_37886:Lnet/minecraft/class_7179$class_7182;
       141: iconst_1
       142: anewarray     #55                 // class net/minecraft/class_7186
       145: dup
       146: iconst_0
       147: new           #55                 // class net/minecraft/class_7186
       150: dup
       151: fconst_0
       152: fconst_0
       153: fconst_1
       154: fconst_0
       155: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
       158: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
       161: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       164: aastore
       165: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
       168: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
       171: ldc           #93                 // String Neck1
       173: new           #14                 // class net/minecraft/class_7179
       176: dup
       177: getstatic     #88                 // Field net/minecraft/class_7179$class_7183.field_37886:Lnet/minecraft/class_7179$class_7182;
       180: iconst_1
       181: anewarray     #55                 // class net/minecraft/class_7186
       184: dup
       185: iconst_0
       186: new           #55                 // class net/minecraft/class_7186
       189: dup
       190: fconst_0
       191: fconst_0
       192: ldc           #94                 // float 0.5f
       194: fconst_0
       195: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
       198: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
       201: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       204: aastore
       205: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
       208: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
       211: ldc           #96                 // String Rattle
       213: new           #14                 // class net/minecraft/class_7179
       216: dup
       217: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
       220: iconst_1
       221: anewarray     #55                 // class net/minecraft/class_7186
       224: dup
       225: iconst_0
       226: new           #55                 // class net/minecraft/class_7186
       229: dup
       230: fconst_0
       231: ldc           #97                 // float 47.5f
       233: fconst_0
       234: fconst_0
       235: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
       238: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
       241: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       244: aastore
       245: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
       248: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
       251: ldc           #99                 // String Neck3
       253: new           #14                 // class net/minecraft/class_7179
       256: dup
       257: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
       260: iconst_5
       261: anewarray     #55                 // class net/minecraft/class_7186
       264: dup
       265: iconst_0
       266: new           #55                 // class net/minecraft/class_7186
       269: dup
       270: fconst_0
       271: fconst_0
       272: ldc           #100                // float -30.0f
       274: fconst_0
       275: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
       278: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
       281: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       284: aastore
       285: dup
       286: iconst_1
       287: new           #55                 // class net/minecraft/class_7186
       290: dup
       291: ldc           #101                // float 0.75f
       293: fconst_0
       294: ldc           #102                // float 2.5f
       296: fconst_0
       297: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
       300: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
       303: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       306: aastore
       307: dup
       308: iconst_2
       309: new           #55                 // class net/minecraft/class_7186
       312: dup
       313: ldc           #70                 // float 1.5f
       315: fconst_0
       316: ldc           #103                // float 30.0f
       318: fconst_0
       319: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
       322: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
       325: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       328: aastore
       329: dup
       330: iconst_3
       331: new           #55                 // class net/minecraft/class_7186
       334: dup
       335: ldc           #104                // float 2.25f
       337: fconst_0
       338: ldc           #105                // float -2.5f
       340: fconst_0
       341: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
       344: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
       347: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       350: aastore
       351: dup
       352: iconst_4
       353: new           #55                 // class net/minecraft/class_7186
       356: dup
       357: ldc           #39                 // float 3.0f
       359: fconst_0
       360: ldc           #100                // float -30.0f
       362: fconst_0
       363: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
       366: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
       369: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       372: aastore
       373: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
       376: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
       379: ldc           #107                // String Body6
       381: new           #14                 // class net/minecraft/class_7179
       384: dup
       385: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
       388: iconst_5
       389: anewarray     #55                 // class net/minecraft/class_7186
       392: dup
       393: iconst_0
       394: new           #55                 // class net/minecraft/class_7186
       397: dup
       398: fconst_0
       399: fconst_0
       400: ldc           #108                // float -35.0f
       402: fconst_0
       403: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
       406: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
       409: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       412: aastore
       413: dup
       414: iconst_1
       415: new           #55                 // class net/minecraft/class_7186
       418: dup
       419: ldc           #101                // float 0.75f
       421: fconst_0
       422: ldc           #109                // float 27.5f
       424: fconst_0
       425: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
       428: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
       431: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       434: aastore
       435: dup
       436: iconst_2
       437: new           #55                 // class net/minecraft/class_7186
       440: dup
       441: ldc           #70                 // float 1.5f
       443: fconst_0
       444: ldc           #110                // float 35.0f
       446: fconst_0
       447: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
       450: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
       453: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       456: aastore
       457: dup
       458: iconst_3
       459: new           #55                 // class net/minecraft/class_7186
       462: dup
       463: ldc           #104                // float 2.25f
       465: fconst_0
       466: ldc           #111                // float -27.5f
       468: fconst_0
       469: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
       472: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
       475: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       478: aastore
       479: dup
       480: iconst_4
       481: new           #55                 // class net/minecraft/class_7186
       484: dup
       485: ldc           #39                 // float 3.0f
       487: fconst_0
       488: ldc           #108                // float -35.0f
       490: fconst_0
       491: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
       494: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
       497: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       500: aastore
       501: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
       504: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
       507: ldc           #113                // String Body7
       509: new           #14                 // class net/minecraft/class_7179
       512: dup
       513: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
       516: iconst_5
       517: anewarray     #55                 // class net/minecraft/class_7186
       520: dup
       521: iconst_0
       522: new           #55                 // class net/minecraft/class_7186
       525: dup
       526: fconst_0
       527: fconst_0
       528: ldc           #114                // float 57.5f
       530: fconst_0
       531: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
       534: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
       537: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       540: aastore
       541: dup
       542: iconst_1
       543: new           #55                 // class net/minecraft/class_7186
       546: dup
       547: ldc           #101                // float 0.75f
       549: fconst_0
       550: ldc           #115                // float 10.0f
       552: fconst_0
       553: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
       556: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
       559: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       562: aastore
       563: dup
       564: iconst_2
       565: new           #55                 // class net/minecraft/class_7186
       568: dup
       569: ldc           #70                 // float 1.5f
       571: fconst_0
       572: ldc           #116                // float -57.5f
       574: fconst_0
       575: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
       578: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
       581: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       584: aastore
       585: dup
       586: iconst_3
       587: new           #55                 // class net/minecraft/class_7186
       590: dup
       591: ldc           #104                // float 2.25f
       593: fconst_0
       594: ldc           #117                // float 5.0f
       596: fconst_0
       597: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
       600: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
       603: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       606: aastore
       607: dup
       608: iconst_4
       609: new           #55                 // class net/minecraft/class_7186
       612: dup
       613: ldc           #39                 // float 3.0f
       615: fconst_0
       616: ldc           #114                // float 57.5f
       618: fconst_0
       619: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
       622: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
       625: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       628: aastore
       629: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
       632: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
       635: ldc           #119                // String Body8
       637: new           #14                 // class net/minecraft/class_7179
       640: dup
       641: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
       644: iconst_5
       645: anewarray     #55                 // class net/minecraft/class_7186
       648: dup
       649: iconst_0
       650: new           #55                 // class net/minecraft/class_7186
       653: dup
       654: fconst_0
       655: fconst_0
       656: ldc           #120                // float 65.0f
       658: fconst_0
       659: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
       662: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
       665: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       668: aastore
       669: dup
       670: iconst_1
       671: new           #55                 // class net/minecraft/class_7186
       674: dup
       675: ldc           #101                // float 0.75f
       677: fconst_0
       678: ldc           #121                // float -25.0f
       680: fconst_0
       681: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
       684: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
       687: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       690: aastore
       691: dup
       692: iconst_2
       693: new           #55                 // class net/minecraft/class_7186
       696: dup
       697: ldc           #70                 // float 1.5f
       699: fconst_0
       700: ldc           #122                // float -65.0f
       702: fconst_0
       703: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
       706: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
       709: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       712: aastore
       713: dup
       714: iconst_3
       715: new           #55                 // class net/minecraft/class_7186
       718: dup
       719: ldc           #104                // float 2.25f
       721: fconst_0
       722: ldc           #123                // float 25.0f
       724: fconst_0
       725: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
       728: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
       731: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       734: aastore
       735: dup
       736: iconst_4
       737: new           #55                 // class net/minecraft/class_7186
       740: dup
       741: ldc           #39                 // float 3.0f
       743: fconst_0
       744: ldc           #120                // float 65.0f
       746: fconst_0
       747: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
       750: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
       753: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       756: aastore
       757: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
       760: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
       763: ldc           #125                // String Body9
       765: new           #14                 // class net/minecraft/class_7179
       768: dup
       769: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
       772: iconst_5
       773: anewarray     #55                 // class net/minecraft/class_7186
       776: dup
       777: iconst_0
       778: new           #55                 // class net/minecraft/class_7186
       781: dup
       782: fconst_0
       783: fconst_0
       784: ldc           #126                // float 22.5f
       786: fconst_0
       787: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
       790: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
       793: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       796: aastore
       797: dup
       798: iconst_1
       799: new           #55                 // class net/minecraft/class_7186
       802: dup
       803: ldc           #101                // float 0.75f
       805: fconst_0
       806: ldc           #127                // float -42.5f
       808: fconst_0
       809: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
       812: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
       815: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       818: aastore
       819: dup
       820: iconst_2
       821: new           #55                 // class net/minecraft/class_7186
       824: dup
       825: ldc           #70                 // float 1.5f
       827: fconst_0
       828: ldc           #128                // float -22.5f
       830: fconst_0
       831: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
       834: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
       837: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       840: aastore
       841: dup
       842: iconst_3
       843: new           #55                 // class net/minecraft/class_7186
       846: dup
       847: ldc           #104                // float 2.25f
       849: fconst_0
       850: ldc           #129                // float 42.5f
       852: fconst_0
       853: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
       856: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
       859: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       862: aastore
       863: dup
       864: iconst_4
       865: new           #55                 // class net/minecraft/class_7186
       868: dup
       869: ldc           #39                 // float 3.0f
       871: fconst_0
       872: ldc           #126                // float 22.5f
       874: fconst_0
       875: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
       878: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
       881: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       884: aastore
       885: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
       888: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
       891: ldc           #131                // String Body10
       893: new           #14                 // class net/minecraft/class_7179
       896: dup
       897: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
       900: iconst_5
       901: anewarray     #55                 // class net/minecraft/class_7186
       904: dup
       905: iconst_0
       906: new           #55                 // class net/minecraft/class_7186
       909: dup
       910: fconst_0
       911: fconst_0
       912: ldc           #108                // float -35.0f
       914: fconst_0
       915: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
       918: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
       921: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       924: aastore
       925: dup
       926: iconst_1
       927: new           #55                 // class net/minecraft/class_7186
       930: dup
       931: ldc           #101                // float 0.75f
       933: fconst_0
       934: ldc           #56                 // float -20.0f
       936: fconst_0
       937: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
       940: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
       943: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       946: aastore
       947: dup
       948: iconst_2
       949: new           #55                 // class net/minecraft/class_7186
       952: dup
       953: ldc           #70                 // float 1.5f
       955: fconst_0
       956: ldc           #110                // float 35.0f
       958: fconst_0
       959: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
       962: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
       965: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       968: aastore
       969: dup
       970: iconst_3
       971: new           #55                 // class net/minecraft/class_7186
       974: dup
       975: ldc           #104                // float 2.25f
       977: fconst_0
       978: ldc           #109                // float 27.5f
       980: fconst_0
       981: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
       984: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
       987: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
       990: aastore
       991: dup
       992: iconst_4
       993: new           #55                 // class net/minecraft/class_7186
       996: dup
       997: ldc           #39                 // float 3.0f
       999: fconst_0
      1000: ldc           #108                // float -35.0f
      1002: fconst_0
      1003: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1006: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1009: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1012: aastore
      1013: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      1016: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      1019: ldc           #133                // String Tail4
      1021: new           #14                 // class net/minecraft/class_7179
      1024: dup
      1025: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      1028: iconst_5
      1029: anewarray     #55                 // class net/minecraft/class_7186
      1032: dup
      1033: iconst_0
      1034: new           #55                 // class net/minecraft/class_7186
      1037: dup
      1038: fconst_0
      1039: fconst_0
      1040: ldc           #134                // float -40.0f
      1042: fconst_0
      1043: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1046: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1049: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1052: aastore
      1053: dup
      1054: iconst_1
      1055: new           #55                 // class net/minecraft/class_7186
      1058: dup
      1059: ldc           #101                // float 0.75f
      1061: fconst_0
      1062: ldc           #135                // float 37.5f
      1064: fconst_0
      1065: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1068: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1071: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1074: aastore
      1075: dup
      1076: iconst_2
      1077: new           #55                 // class net/minecraft/class_7186
      1080: dup
      1081: ldc           #70                 // float 1.5f
      1083: fconst_0
      1084: ldc           #71                 // float 20.0f
      1086: fconst_0
      1087: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1090: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1093: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1096: aastore
      1097: dup
      1098: iconst_3
      1099: new           #55                 // class net/minecraft/class_7186
      1102: dup
      1103: ldc           #104                // float 2.25f
      1105: fconst_0
      1106: ldc           #100                // float -30.0f
      1108: fconst_0
      1109: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1112: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1115: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1118: aastore
      1119: dup
      1120: iconst_4
      1121: new           #55                 // class net/minecraft/class_7186
      1124: dup
      1125: ldc           #39                 // float 3.0f
      1127: fconst_0
      1128: ldc           #134                // float -40.0f
      1130: fconst_0
      1131: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1134: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1137: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1140: aastore
      1141: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      1144: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      1147: ldc           #137                // String Tail5
      1149: new           #14                 // class net/minecraft/class_7179
      1152: dup
      1153: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      1156: iconst_5
      1157: anewarray     #55                 // class net/minecraft/class_7186
      1160: dup
      1161: iconst_0
      1162: new           #55                 // class net/minecraft/class_7186
      1165: dup
      1166: fconst_0
      1167: fconst_0
      1168: ldc           #138                // float -50.0f
      1170: fconst_0
      1171: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1174: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1177: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1180: aastore
      1181: dup
      1182: iconst_1
      1183: new           #55                 // class net/minecraft/class_7186
      1186: dup
      1187: ldc           #101                // float 0.75f
      1189: fconst_0
      1190: ldc           #109                // float 27.5f
      1192: fconst_0
      1193: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1196: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1199: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1202: aastore
      1203: dup
      1204: iconst_2
      1205: new           #55                 // class net/minecraft/class_7186
      1208: dup
      1209: ldc           #70                 // float 1.5f
      1211: fconst_0
      1212: ldc           #139                // float 50.0f
      1214: fconst_0
      1215: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1218: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1221: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1224: aastore
      1225: dup
      1226: iconst_3
      1227: new           #55                 // class net/minecraft/class_7186
      1230: dup
      1231: ldc           #104                // float 2.25f
      1233: fconst_0
      1234: ldc           #140                // float -45.0f
      1236: fconst_0
      1237: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1240: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1243: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1246: aastore
      1247: dup
      1248: iconst_4
      1249: new           #55                 // class net/minecraft/class_7186
      1252: dup
      1253: ldc           #39                 // float 3.0f
      1255: fconst_0
      1256: ldc           #138                // float -50.0f
      1258: fconst_0
      1259: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1262: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1265: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1268: aastore
      1269: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      1272: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      1275: ldc           #142                // String Tail6
      1277: new           #14                 // class net/minecraft/class_7179
      1280: dup
      1281: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      1284: iconst_5
      1285: anewarray     #55                 // class net/minecraft/class_7186
      1288: dup
      1289: iconst_0
      1290: new           #55                 // class net/minecraft/class_7186
      1293: dup
      1294: fconst_0
      1295: ldc           #143                // float 25.8915f
      1297: ldc           #144                // float 48.6554f
      1299: fconst_0
      1300: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1303: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1306: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1309: aastore
      1310: dup
      1311: iconst_1
      1312: new           #55                 // class net/minecraft/class_7186
      1315: dup
      1316: ldc           #101                // float 0.75f
      1318: ldc           #145                // float 18.2499f
      1320: ldc           #146                // float -8.1809f
      1322: fconst_0
      1323: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1326: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1329: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1332: aastore
      1333: dup
      1334: iconst_2
      1335: new           #55                 // class net/minecraft/class_7186
      1338: dup
      1339: ldc           #70                 // float 1.5f
      1341: ldc           #147                // float 19.835f
      1343: ldc           #148                // float -31.775f
      1345: fconst_0
      1346: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1349: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1352: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1355: aastore
      1356: dup
      1357: iconst_3
      1358: new           #55                 // class net/minecraft/class_7186
      1361: dup
      1362: ldc           #104                // float 2.25f
      1364: ldc           #149                // float 18.9284f
      1366: ldc           #150                // float -13.0684f
      1368: fconst_0
      1369: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1372: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1375: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1378: aastore
      1379: dup
      1380: iconst_4
      1381: new           #55                 // class net/minecraft/class_7186
      1384: dup
      1385: ldc           #39                 // float 3.0f
      1387: ldc           #151                // float 25.8804f
      1389: ldc           #152                // float 45.9705f
      1391: fconst_0
      1392: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1395: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1398: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1401: aastore
      1402: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      1405: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      1408: invokevirtual #156                // Method net/minecraft/class_7184$class_7185.method_41821:()Lnet/minecraft/class_7184;
      1411: putstatic     #158                // Field ANIM_RATTLESNAKE_WALK2:Lnet/minecraft/class_7184;
      1414: fconst_1
      1415: invokestatic  #43                 // Method net/minecraft/class_7184$class_7185.method_41818:(F)Lnet/minecraft/class_7184$class_7185;
      1418: invokevirtual #47                 // Method net/minecraft/class_7184$class_7185.method_41817:()Lnet/minecraft/class_7184$class_7185;
      1421: ldc           #49                 // String RattleSnake
      1423: new           #14                 // class net/minecraft/class_7179
      1426: dup
      1427: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      1430: iconst_3
      1431: anewarray     #55                 // class net/minecraft/class_7186
      1434: dup
      1435: iconst_0
      1436: new           #55                 // class net/minecraft/class_7186
      1439: dup
      1440: fconst_0
      1441: fconst_0
      1442: ldc           #82                 // float -10.0f
      1444: fconst_0
      1445: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1448: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1451: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1454: aastore
      1455: dup
      1456: iconst_1
      1457: new           #55                 // class net/minecraft/class_7186
      1460: dup
      1461: ldc           #94                 // float 0.5f
      1463: fconst_0
      1464: ldc           #115                // float 10.0f
      1466: fconst_0
      1467: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1470: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1473: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1476: aastore
      1477: dup
      1478: iconst_2
      1479: new           #55                 // class net/minecraft/class_7186
      1482: dup
      1483: fconst_1
      1484: fconst_0
      1485: ldc           #82                 // float -10.0f
      1487: fconst_0
      1488: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1491: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1494: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1497: aastore
      1498: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      1501: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      1504: ldc           #81                 // String Head
      1506: new           #14                 // class net/minecraft/class_7179
      1509: dup
      1510: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      1513: iconst_1
      1514: anewarray     #55                 // class net/minecraft/class_7186
      1517: dup
      1518: iconst_0
      1519: new           #55                 // class net/minecraft/class_7186
      1522: dup
      1523: fconst_0
      1524: ldc           #159                // float -5.0f
      1526: fconst_0
      1527: fconst_0
      1528: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1531: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      1534: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1537: aastore
      1538: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      1541: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      1544: ldc           #81                 // String Head
      1546: new           #14                 // class net/minecraft/class_7179
      1549: dup
      1550: getstatic     #88                 // Field net/minecraft/class_7179$class_7183.field_37886:Lnet/minecraft/class_7179$class_7182;
      1553: iconst_1
      1554: anewarray     #55                 // class net/minecraft/class_7186
      1557: dup
      1558: iconst_0
      1559: new           #55                 // class net/minecraft/class_7186
      1562: dup
      1563: fconst_0
      1564: fconst_0
      1565: fconst_1
      1566: fconst_0
      1567: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      1570: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      1573: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1576: aastore
      1577: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      1580: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      1583: ldc           #161                // String Body
      1585: new           #14                 // class net/minecraft/class_7179
      1588: dup
      1589: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      1592: iconst_5
      1593: anewarray     #55                 // class net/minecraft/class_7186
      1596: dup
      1597: iconst_0
      1598: new           #55                 // class net/minecraft/class_7186
      1601: dup
      1602: fconst_0
      1603: fconst_0
      1604: ldc           #162                // float -17.5f
      1606: fconst_0
      1607: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1610: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1613: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1616: aastore
      1617: dup
      1618: iconst_1
      1619: new           #55                 // class net/minecraft/class_7186
      1622: dup
      1623: ldc           #163                // float 0.25f
      1625: fconst_0
      1626: ldc           #117                // float 5.0f
      1628: fconst_0
      1629: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1632: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1635: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1638: aastore
      1639: dup
      1640: iconst_2
      1641: new           #55                 // class net/minecraft/class_7186
      1644: dup
      1645: ldc           #94                 // float 0.5f
      1647: fconst_0
      1648: ldc           #164                // float 17.5f
      1650: fconst_0
      1651: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1654: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1657: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1660: aastore
      1661: dup
      1662: iconst_3
      1663: new           #55                 // class net/minecraft/class_7186
      1666: dup
      1667: ldc           #101                // float 0.75f
      1669: fconst_0
      1670: ldc           #159                // float -5.0f
      1672: fconst_0
      1673: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1676: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1679: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1682: aastore
      1683: dup
      1684: iconst_4
      1685: new           #55                 // class net/minecraft/class_7186
      1688: dup
      1689: fconst_1
      1690: fconst_0
      1691: ldc           #162                // float -17.5f
      1693: fconst_0
      1694: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1697: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1700: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1703: aastore
      1704: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      1707: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      1710: ldc           #93                 // String Neck1
      1712: new           #14                 // class net/minecraft/class_7179
      1715: dup
      1716: getstatic     #88                 // Field net/minecraft/class_7179$class_7183.field_37886:Lnet/minecraft/class_7179$class_7182;
      1719: iconst_1
      1720: anewarray     #55                 // class net/minecraft/class_7186
      1723: dup
      1724: iconst_0
      1725: new           #55                 // class net/minecraft/class_7186
      1728: dup
      1729: fconst_0
      1730: fconst_0
      1731: ldc           #94                 // float 0.5f
      1733: fconst_0
      1734: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      1737: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      1740: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1743: aastore
      1744: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      1747: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      1750: ldc           #96                 // String Rattle
      1752: new           #14                 // class net/minecraft/class_7179
      1755: dup
      1756: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      1759: iconst_1
      1760: anewarray     #55                 // class net/minecraft/class_7186
      1763: dup
      1764: iconst_0
      1765: new           #55                 // class net/minecraft/class_7186
      1768: dup
      1769: fconst_0
      1770: ldc           #165                // float 40.0f
      1772: fconst_0
      1773: fconst_0
      1774: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1777: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      1780: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1783: aastore
      1784: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      1787: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      1790: ldc           #99                 // String Neck3
      1792: new           #14                 // class net/minecraft/class_7179
      1795: dup
      1796: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      1799: iconst_5
      1800: anewarray     #55                 // class net/minecraft/class_7186
      1803: dup
      1804: iconst_0
      1805: new           #55                 // class net/minecraft/class_7186
      1808: dup
      1809: fconst_0
      1810: fconst_0
      1811: ldc           #128                // float -22.5f
      1813: fconst_0
      1814: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1817: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1820: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1823: aastore
      1824: dup
      1825: iconst_1
      1826: new           #55                 // class net/minecraft/class_7186
      1829: dup
      1830: ldc           #163                // float 0.25f
      1832: fconst_0
      1833: ldc           #121                // float -25.0f
      1835: fconst_0
      1836: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1839: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1842: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1845: aastore
      1846: dup
      1847: iconst_2
      1848: new           #55                 // class net/minecraft/class_7186
      1851: dup
      1852: ldc           #94                 // float 0.5f
      1854: fconst_0
      1855: ldc           #126                // float 22.5f
      1857: fconst_0
      1858: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1861: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1864: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1867: aastore
      1868: dup
      1869: iconst_3
      1870: new           #55                 // class net/minecraft/class_7186
      1873: dup
      1874: ldc           #101                // float 0.75f
      1876: fconst_0
      1877: ldc           #123                // float 25.0f
      1879: fconst_0
      1880: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1883: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1886: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1889: aastore
      1890: dup
      1891: iconst_4
      1892: new           #55                 // class net/minecraft/class_7186
      1895: dup
      1896: fconst_1
      1897: fconst_0
      1898: ldc           #128                // float -22.5f
      1900: fconst_0
      1901: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1904: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1907: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1910: aastore
      1911: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      1914: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      1917: ldc           #107                // String Body6
      1919: new           #14                 // class net/minecraft/class_7179
      1922: dup
      1923: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      1926: iconst_5
      1927: anewarray     #55                 // class net/minecraft/class_7186
      1930: dup
      1931: iconst_0
      1932: new           #55                 // class net/minecraft/class_7186
      1935: dup
      1936: fconst_0
      1937: fconst_0
      1938: ldc           #166                // float 70.0f
      1940: fconst_0
      1941: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1944: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1947: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1950: aastore
      1951: dup
      1952: iconst_1
      1953: new           #55                 // class net/minecraft/class_7186
      1956: dup
      1957: ldc           #163                // float 0.25f
      1959: fconst_0
      1960: ldc           #117                // float 5.0f
      1962: fconst_0
      1963: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1966: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1969: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1972: aastore
      1973: dup
      1974: iconst_2
      1975: new           #55                 // class net/minecraft/class_7186
      1978: dup
      1979: ldc           #94                 // float 0.5f
      1981: fconst_0
      1982: ldc           #167                // float -70.0f
      1984: fconst_0
      1985: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      1988: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      1991: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      1994: aastore
      1995: dup
      1996: iconst_3
      1997: new           #55                 // class net/minecraft/class_7186
      2000: dup
      2001: ldc           #101                // float 0.75f
      2003: fconst_0
      2004: ldc           #159                // float -5.0f
      2006: fconst_0
      2007: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2010: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2013: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2016: aastore
      2017: dup
      2018: iconst_4
      2019: new           #55                 // class net/minecraft/class_7186
      2022: dup
      2023: fconst_1
      2024: fconst_0
      2025: ldc           #166                // float 70.0f
      2027: fconst_0
      2028: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2031: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2034: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2037: aastore
      2038: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      2041: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      2044: ldc           #113                // String Body7
      2046: new           #14                 // class net/minecraft/class_7179
      2049: dup
      2050: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      2053: iconst_5
      2054: anewarray     #55                 // class net/minecraft/class_7186
      2057: dup
      2058: iconst_0
      2059: new           #55                 // class net/minecraft/class_7186
      2062: dup
      2063: fconst_0
      2064: fconst_0
      2065: ldc           #56                 // float -20.0f
      2067: fconst_0
      2068: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2071: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2074: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2077: aastore
      2078: dup
      2079: iconst_1
      2080: new           #55                 // class net/minecraft/class_7186
      2083: dup
      2084: ldc           #163                // float 0.25f
      2086: fconst_0
      2087: ldc           #71                 // float 20.0f
      2089: fconst_0
      2090: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2093: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2096: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2099: aastore
      2100: dup
      2101: iconst_2
      2102: new           #55                 // class net/minecraft/class_7186
      2105: dup
      2106: ldc           #94                 // float 0.5f
      2108: fconst_0
      2109: ldc           #71                 // float 20.0f
      2111: fconst_0
      2112: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2115: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2118: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2121: aastore
      2122: dup
      2123: iconst_3
      2124: new           #55                 // class net/minecraft/class_7186
      2127: dup
      2128: ldc           #101                // float 0.75f
      2130: fconst_0
      2131: ldc           #56                 // float -20.0f
      2133: fconst_0
      2134: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2137: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2140: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2143: aastore
      2144: dup
      2145: iconst_4
      2146: new           #55                 // class net/minecraft/class_7186
      2149: dup
      2150: fconst_1
      2151: fconst_0
      2152: ldc           #56                 // float -20.0f
      2154: fconst_0
      2155: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2158: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2161: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2164: aastore
      2165: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      2168: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      2171: ldc           #119                // String Body8
      2173: new           #14                 // class net/minecraft/class_7179
      2176: dup
      2177: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      2180: iconst_5
      2181: anewarray     #55                 // class net/minecraft/class_7186
      2184: dup
      2185: iconst_0
      2186: new           #55                 // class net/minecraft/class_7186
      2189: dup
      2190: fconst_0
      2191: fconst_0
      2192: ldc           #167                // float -70.0f
      2194: fconst_0
      2195: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2198: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2201: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2204: aastore
      2205: dup
      2206: iconst_1
      2207: new           #55                 // class net/minecraft/class_7186
      2210: dup
      2211: ldc           #163                // float 0.25f
      2213: fconst_0
      2214: ldc           #126                // float 22.5f
      2216: fconst_0
      2217: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2220: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2223: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2226: aastore
      2227: dup
      2228: iconst_2
      2229: new           #55                 // class net/minecraft/class_7186
      2232: dup
      2233: ldc           #94                 // float 0.5f
      2235: fconst_0
      2236: ldc           #166                // float 70.0f
      2238: fconst_0
      2239: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2242: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2245: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2248: aastore
      2249: dup
      2250: iconst_3
      2251: new           #55                 // class net/minecraft/class_7186
      2254: dup
      2255: ldc           #101                // float 0.75f
      2257: fconst_0
      2258: ldc           #128                // float -22.5f
      2260: fconst_0
      2261: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2264: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2267: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2270: aastore
      2271: dup
      2272: iconst_4
      2273: new           #55                 // class net/minecraft/class_7186
      2276: dup
      2277: fconst_1
      2278: fconst_0
      2279: ldc           #167                // float -70.0f
      2281: fconst_0
      2282: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2285: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2288: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2291: aastore
      2292: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      2295: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      2298: ldc           #125                // String Body9
      2300: new           #14                 // class net/minecraft/class_7179
      2303: dup
      2304: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      2307: iconst_5
      2308: anewarray     #55                 // class net/minecraft/class_7186
      2311: dup
      2312: iconst_0
      2313: new           #55                 // class net/minecraft/class_7186
      2316: dup
      2317: fconst_0
      2318: fconst_0
      2319: ldc           #97                 // float 47.5f
      2321: fconst_0
      2322: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2325: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2328: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2331: aastore
      2332: dup
      2333: iconst_1
      2334: new           #55                 // class net/minecraft/class_7186
      2337: dup
      2338: ldc           #163                // float 0.25f
      2340: fconst_0
      2341: ldc           #138                // float -50.0f
      2343: fconst_0
      2344: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2347: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2350: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2353: aastore
      2354: dup
      2355: iconst_2
      2356: new           #55                 // class net/minecraft/class_7186
      2359: dup
      2360: ldc           #94                 // float 0.5f
      2362: fconst_0
      2363: ldc           #168                // float -47.5f
      2365: fconst_0
      2366: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2369: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2372: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2375: aastore
      2376: dup
      2377: iconst_3
      2378: new           #55                 // class net/minecraft/class_7186
      2381: dup
      2382: ldc           #101                // float 0.75f
      2384: fconst_0
      2385: ldc           #139                // float 50.0f
      2387: fconst_0
      2388: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2391: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2394: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2397: aastore
      2398: dup
      2399: iconst_4
      2400: new           #55                 // class net/minecraft/class_7186
      2403: dup
      2404: fconst_1
      2405: fconst_0
      2406: ldc           #97                 // float 47.5f
      2408: fconst_0
      2409: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2412: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2415: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2418: aastore
      2419: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      2422: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      2425: ldc           #131                // String Body10
      2427: new           #14                 // class net/minecraft/class_7179
      2430: dup
      2431: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      2434: iconst_5
      2435: anewarray     #55                 // class net/minecraft/class_7186
      2438: dup
      2439: iconst_0
      2440: new           #55                 // class net/minecraft/class_7186
      2443: dup
      2444: fconst_0
      2445: fconst_0
      2446: ldc           #114                // float 57.5f
      2448: fconst_0
      2449: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2452: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2455: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2458: aastore
      2459: dup
      2460: iconst_1
      2461: new           #55                 // class net/minecraft/class_7186
      2464: dup
      2465: ldc           #163                // float 0.25f
      2467: fconst_0
      2468: ldc           #111                // float -27.5f
      2470: fconst_0
      2471: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2474: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2477: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2480: aastore
      2481: dup
      2482: iconst_2
      2483: new           #55                 // class net/minecraft/class_7186
      2486: dup
      2487: ldc           #94                 // float 0.5f
      2489: fconst_0
      2490: ldc           #116                // float -57.5f
      2492: fconst_0
      2493: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2496: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2499: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2502: aastore
      2503: dup
      2504: iconst_3
      2505: new           #55                 // class net/minecraft/class_7186
      2508: dup
      2509: ldc           #101                // float 0.75f
      2511: fconst_0
      2512: ldc           #109                // float 27.5f
      2514: fconst_0
      2515: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2518: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2521: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2524: aastore
      2525: dup
      2526: iconst_4
      2527: new           #55                 // class net/minecraft/class_7186
      2530: dup
      2531: fconst_1
      2532: fconst_0
      2533: ldc           #114                // float 57.5f
      2535: fconst_0
      2536: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2539: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2542: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2545: aastore
      2546: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      2549: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      2552: ldc           #133                // String Tail4
      2554: new           #14                 // class net/minecraft/class_7179
      2557: dup
      2558: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      2561: iconst_5
      2562: anewarray     #55                 // class net/minecraft/class_7186
      2565: dup
      2566: iconst_0
      2567: new           #55                 // class net/minecraft/class_7186
      2570: dup
      2571: fconst_0
      2572: fconst_0
      2573: ldc           #162                // float -17.5f
      2575: fconst_0
      2576: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2579: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2582: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2585: aastore
      2586: dup
      2587: iconst_1
      2588: new           #55                 // class net/minecraft/class_7186
      2591: dup
      2592: ldc           #163                // float 0.25f
      2594: fconst_0
      2595: ldc           #139                // float 50.0f
      2597: fconst_0
      2598: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2601: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2604: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2607: aastore
      2608: dup
      2609: iconst_2
      2610: new           #55                 // class net/minecraft/class_7186
      2613: dup
      2614: ldc           #94                 // float 0.5f
      2616: fconst_0
      2617: ldc           #164                // float 17.5f
      2619: fconst_0
      2620: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2623: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2626: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2629: aastore
      2630: dup
      2631: iconst_3
      2632: new           #55                 // class net/minecraft/class_7186
      2635: dup
      2636: ldc           #101                // float 0.75f
      2638: fconst_0
      2639: ldc           #138                // float -50.0f
      2641: fconst_0
      2642: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2645: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2648: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2651: aastore
      2652: dup
      2653: iconst_4
      2654: new           #55                 // class net/minecraft/class_7186
      2657: dup
      2658: fconst_1
      2659: fconst_0
      2660: ldc           #162                // float -17.5f
      2662: fconst_0
      2663: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2666: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2669: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2672: aastore
      2673: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      2676: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      2679: ldc           #137                // String Tail5
      2681: new           #14                 // class net/minecraft/class_7179
      2684: dup
      2685: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      2688: iconst_5
      2689: anewarray     #55                 // class net/minecraft/class_7186
      2692: dup
      2693: iconst_0
      2694: new           #55                 // class net/minecraft/class_7186
      2697: dup
      2698: fconst_0
      2699: fconst_0
      2700: ldc           #100                // float -30.0f
      2702: fconst_0
      2703: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2706: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2709: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2712: aastore
      2713: dup
      2714: iconst_1
      2715: new           #55                 // class net/minecraft/class_7186
      2718: dup
      2719: ldc           #163                // float 0.25f
      2721: fconst_0
      2722: ldc           #71                 // float 20.0f
      2724: fconst_0
      2725: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2728: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2731: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2734: aastore
      2735: dup
      2736: iconst_2
      2737: new           #55                 // class net/minecraft/class_7186
      2740: dup
      2741: ldc           #94                 // float 0.5f
      2743: fconst_0
      2744: ldc           #103                // float 30.0f
      2746: fconst_0
      2747: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2750: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2753: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2756: aastore
      2757: dup
      2758: iconst_3
      2759: new           #55                 // class net/minecraft/class_7186
      2762: dup
      2763: ldc           #101                // float 0.75f
      2765: fconst_0
      2766: ldc           #56                 // float -20.0f
      2768: fconst_0
      2769: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2772: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2775: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2778: aastore
      2779: dup
      2780: iconst_4
      2781: new           #55                 // class net/minecraft/class_7186
      2784: dup
      2785: fconst_1
      2786: fconst_0
      2787: ldc           #100                // float -30.0f
      2789: fconst_0
      2790: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2793: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      2796: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2799: aastore
      2800: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      2803: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      2806: ldc           #142                // String Tail6
      2808: new           #14                 // class net/minecraft/class_7179
      2811: dup
      2812: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      2815: iconst_1
      2816: anewarray     #55                 // class net/minecraft/class_7186
      2819: dup
      2820: iconst_0
      2821: new           #55                 // class net/minecraft/class_7186
      2824: dup
      2825: fconst_0
      2826: ldc           #169                // float 15.0f
      2828: fconst_0
      2829: fconst_0
      2830: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2833: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      2836: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2839: aastore
      2840: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      2843: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      2846: invokevirtual #156                // Method net/minecraft/class_7184$class_7185.method_41821:()Lnet/minecraft/class_7184;
      2849: putstatic     #171                // Field ANIM_RATTLESNAKE_WALK:Lnet/minecraft/class_7184;
      2852: ldc           #172                // float 0.0417f
      2854: invokestatic  #43                 // Method net/minecraft/class_7184$class_7185.method_41818:(F)Lnet/minecraft/class_7184$class_7185;
      2857: invokevirtual #47                 // Method net/minecraft/class_7184$class_7185.method_41817:()Lnet/minecraft/class_7184$class_7185;
      2860: ldc           #49                 // String RattleSnake
      2862: new           #14                 // class net/minecraft/class_7179
      2865: dup
      2866: getstatic     #88                 // Field net/minecraft/class_7179$class_7183.field_37886:Lnet/minecraft/class_7179$class_7182;
      2869: iconst_1
      2870: anewarray     #55                 // class net/minecraft/class_7186
      2873: dup
      2874: iconst_0
      2875: new           #55                 // class net/minecraft/class_7186
      2878: dup
      2879: fconst_0
      2880: fconst_0
      2881: fconst_0
      2882: ldc           #173                // float 9.75f
      2884: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      2887: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      2890: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2893: aastore
      2894: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      2897: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      2900: ldc           #81                 // String Head
      2902: new           #14                 // class net/minecraft/class_7179
      2905: dup
      2906: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      2909: iconst_1
      2910: anewarray     #55                 // class net/minecraft/class_7186
      2913: dup
      2914: iconst_0
      2915: new           #55                 // class net/minecraft/class_7186
      2918: dup
      2919: fconst_0
      2920: ldc           #159                // float -5.0f
      2922: fconst_0
      2923: fconst_0
      2924: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      2927: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      2930: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2933: aastore
      2934: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      2937: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      2940: ldc           #81                 // String Head
      2942: new           #14                 // class net/minecraft/class_7179
      2945: dup
      2946: getstatic     #88                 // Field net/minecraft/class_7179$class_7183.field_37886:Lnet/minecraft/class_7179$class_7182;
      2949: iconst_1
      2950: anewarray     #55                 // class net/minecraft/class_7186
      2953: dup
      2954: iconst_0
      2955: new           #55                 // class net/minecraft/class_7186
      2958: dup
      2959: fconst_0
      2960: fconst_0
      2961: ldc           #94                 // float 0.5f
      2963: fconst_0
      2964: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      2967: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      2970: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      2973: aastore
      2974: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      2977: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      2980: ldc           #96                 // String Rattle
      2982: new           #14                 // class net/minecraft/class_7179
      2985: dup
      2986: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      2989: iconst_1
      2990: anewarray     #55                 // class net/minecraft/class_7186
      2993: dup
      2994: iconst_0
      2995: new           #55                 // class net/minecraft/class_7186
      2998: dup
      2999: fconst_0
      3000: ldc           #165                // float 40.0f
      3002: fconst_0
      3003: fconst_0
      3004: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      3007: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      3010: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      3013: aastore
      3014: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      3017: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      3020: ldc           #99                 // String Neck3
      3022: new           #14                 // class net/minecraft/class_7179
      3025: dup
      3026: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      3029: iconst_1
      3030: anewarray     #55                 // class net/minecraft/class_7186
      3033: dup
      3034: iconst_0
      3035: new           #55                 // class net/minecraft/class_7186
      3038: dup
      3039: fconst_0
      3040: fconst_0
      3041: ldc           #100                // float -30.0f
      3043: fconst_0
      3044: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      3047: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      3050: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      3053: aastore
      3054: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      3057: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      3060: ldc           #107                // String Body6
      3062: new           #14                 // class net/minecraft/class_7179
      3065: dup
      3066: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      3069: iconst_1
      3070: anewarray     #55                 // class net/minecraft/class_7186
      3073: dup
      3074: iconst_0
      3075: new           #55                 // class net/minecraft/class_7186
      3078: dup
      3079: fconst_0
      3080: fconst_0
      3081: ldc           #138                // float -50.0f
      3083: fconst_0
      3084: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      3087: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      3090: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      3093: aastore
      3094: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      3097: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      3100: ldc           #113                // String Body7
      3102: new           #14                 // class net/minecraft/class_7179
      3105: dup
      3106: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      3109: iconst_1
      3110: anewarray     #55                 // class net/minecraft/class_7186
      3113: dup
      3114: iconst_0
      3115: new           #55                 // class net/minecraft/class_7186
      3118: dup
      3119: fconst_0
      3120: fconst_0
      3121: ldc           #174                // float 60.0f
      3123: fconst_0
      3124: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      3127: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      3130: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      3133: aastore
      3134: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      3137: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      3140: ldc           #119                // String Body8
      3142: new           #14                 // class net/minecraft/class_7179
      3145: dup
      3146: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      3149: iconst_1
      3150: anewarray     #55                 // class net/minecraft/class_7186
      3153: dup
      3154: iconst_0
      3155: new           #55                 // class net/minecraft/class_7186
      3158: dup
      3159: fconst_0
      3160: fconst_0
      3161: ldc           #175                // float 90.0f
      3163: fconst_0
      3164: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      3167: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      3170: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      3173: aastore
      3174: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      3177: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      3180: ldc           #125                // String Body9
      3182: new           #14                 // class net/minecraft/class_7179
      3185: dup
      3186: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      3189: iconst_1
      3190: anewarray     #55                 // class net/minecraft/class_7186
      3193: dup
      3194: iconst_0
      3195: new           #55                 // class net/minecraft/class_7186
      3198: dup
      3199: fconst_0
      3200: fconst_0
      3201: ldc           #103                // float 30.0f
      3203: fconst_0
      3204: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      3207: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      3210: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      3213: aastore
      3214: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      3217: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      3220: ldc           #131                // String Body10
      3222: new           #14                 // class net/minecraft/class_7179
      3225: dup
      3226: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      3229: iconst_1
      3230: anewarray     #55                 // class net/minecraft/class_7186
      3233: dup
      3234: iconst_0
      3235: new           #55                 // class net/minecraft/class_7186
      3238: dup
      3239: fconst_0
      3240: fconst_0
      3241: ldc           #176                // float -90.0f
      3243: fconst_0
      3244: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      3247: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      3250: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      3253: aastore
      3254: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      3257: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      3260: ldc           #133                // String Tail4
      3262: new           #14                 // class net/minecraft/class_7179
      3265: dup
      3266: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      3269: iconst_1
      3270: anewarray     #55                 // class net/minecraft/class_7186
      3273: dup
      3274: iconst_0
      3275: new           #55                 // class net/minecraft/class_7186
      3278: dup
      3279: fconst_0
      3280: fconst_0
      3281: ldc           #100                // float -30.0f
      3283: fconst_0
      3284: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      3287: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      3290: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      3293: aastore
      3294: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      3297: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      3300: ldc           #137                // String Tail5
      3302: new           #14                 // class net/minecraft/class_7179
      3305: dup
      3306: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      3309: iconst_1
      3310: anewarray     #55                 // class net/minecraft/class_7186
      3313: dup
      3314: iconst_0
      3315: new           #55                 // class net/minecraft/class_7186
      3318: dup
      3319: fconst_0
      3320: fconst_0
      3321: ldc           #176                // float -90.0f
      3323: fconst_0
      3324: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      3327: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      3330: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      3333: aastore
      3334: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      3337: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      3340: ldc           #142                // String Tail6
      3342: new           #14                 // class net/minecraft/class_7179
      3345: dup
      3346: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      3349: iconst_1
      3350: anewarray     #55                 // class net/minecraft/class_7186
      3353: dup
      3354: iconst_0
      3355: new           #55                 // class net/minecraft/class_7186
      3358: dup
      3359: fconst_0
      3360: ldc           #177                // float 15.7664f
      3362: ldc           #178                // float 49.5098f
      3364: ldc           #179                // float 0.2089f
      3366: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      3369: getstatic     #66                 // Field net/minecraft/class_7179$class_7181.field_37885:Lnet/minecraft/class_7179$class_7180;
      3372: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      3375: aastore
      3376: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      3379: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      3382: invokevirtual #156                // Method net/minecraft/class_7184$class_7185.method_41821:()Lnet/minecraft/class_7184;
      3385: putstatic     #181                // Field ANIM_RATTLESNAKE_STAND:Lnet/minecraft/class_7184;
      3388: ldc           #172                // float 0.0417f
      3390: invokestatic  #43                 // Method net/minecraft/class_7184$class_7185.method_41818:(F)Lnet/minecraft/class_7184$class_7185;
      3393: invokevirtual #47                 // Method net/minecraft/class_7184$class_7185.method_41817:()Lnet/minecraft/class_7184$class_7185;
      3396: ldc           #81                 // String Head
      3398: new           #14                 // class net/minecraft/class_7179
      3401: dup
      3402: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      3405: iconst_1
      3406: anewarray     #55                 // class net/minecraft/class_7186
      3409: dup
      3410: iconst_0
      3411: new           #55                 // class net/minecraft/class_7186
      3414: dup
      3415: fconst_0
      3416: ldc           #162                // float -17.5f
      3418: fconst_0
      3419: fconst_0
      3420: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      3423: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      3426: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      3429: aastore
      3430: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      3433: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      3436: ldc           #81                 // String Head
      3438: new           #14                 // class net/minecraft/class_7179
      3441: dup
      3442: getstatic     #88                 // Field net/minecraft/class_7179$class_7183.field_37886:Lnet/minecraft/class_7179$class_7182;
      3445: iconst_1
      3446: anewarray     #55                 // class net/minecraft/class_7186
      3449: dup
      3450: iconst_0
      3451: new           #55                 // class net/minecraft/class_7186
      3454: dup
      3455: fconst_0
      3456: ldc           #182                // float 5.5f
      3458: ldc           #183                // float 6.75f
      3460: ldc           #184                // float 18.25f
      3462: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      3465: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      3468: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      3471: aastore
      3472: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      3475: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      3478: ldc           #93                 // String Neck1
      3480: new           #14                 // class net/minecraft/class_7179
      3483: dup
      3484: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      3487: iconst_1
      3488: anewarray     #55                 // class net/minecraft/class_7186
      3491: dup
      3492: iconst_0
      3493: new           #55                 // class net/minecraft/class_7186
      3496: dup
      3497: fconst_0
      3498: ldc           #71                 // float 20.0f
      3500: ldc           #103                // float 30.0f
      3502: fconst_0
      3503: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      3506: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      3509: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      3512: aastore
      3513: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      3516: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      3519: ldc           #93                 // String Neck1
      3521: new           #14                 // class net/minecraft/class_7179
      3524: dup
      3525: getstatic     #88                 // Field net/minecraft/class_7179$class_7183.field_37886:Lnet/minecraft/class_7179$class_7182;
      3528: iconst_1
      3529: anewarray     #55                 // class net/minecraft/class_7186
      3532: dup
      3533: iconst_0
      3534: new           #55                 // class net/minecraft/class_7186
      3537: dup
      3538: fconst_0
      3539: ldc           #185                // float 7.0f
      3541: ldc           #186                // float 8.0f
      3543: ldc           #164                // float 17.5f
      3545: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      3548: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      3551: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      3554: aastore
      3555: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      3558: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      3561: ldc           #188                // String Neck2
      3563: new           #14                 // class net/minecraft/class_7179
      3566: dup
      3567: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      3570: iconst_1
      3571: anewarray     #55                 // class net/minecraft/class_7186
      3574: dup
      3575: iconst_0
      3576: new           #55                 // class net/minecraft/class_7186
      3579: dup
      3580: fconst_0
      3581: ldc           #189                // float -6.2607f
      3583: ldc           #190                // float -42.4066f
      3585: ldc           #191                // float 2.3137f
      3587: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      3590: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      3593: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      3596: aastore
      3597: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      3600: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      3603: ldc           #188                // String Neck2
      3605: new           #14                 // class net/minecraft/class_7179
      3608: dup
      3609: getstatic     #88                 // Field net/minecraft/class_7179$class_7183.field_37886:Lnet/minecraft/class_7179$class_7182;
      3612: iconst_1
      3613: anewarray     #55                 // class net/minecraft/class_7186
      3616: dup
      3617: iconst_0
      3618: new           #55                 // class net/minecraft/class_7186
      3621: dup
      3622: fconst_0
      3623: ldc           #192                // float 6.5f
      3625: ldc           #186                // float 8.0f
      3627: ldc           #164                // float 17.5f
      3629: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      3632: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      3635: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      3638: aastore
      3639: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      3642: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      3645: ldc           #194                // String Body1
      3647: new           #14                 // class net/minecraft/class_7179
      3650: dup
      3651: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      3654: iconst_1
      3655: anewarray     #55                 // class net/minecraft/class_7186
      3658: dup
      3659: iconst_0
      3660: new           #55                 // class net/minecraft/class_7186
      3663: dup
      3664: fconst_0
      3665: ldc           #195                // float 155.0f
      3667: ldc           #159                // float -5.0f
      3669: ldc           #196                // float -180.0f
      3671: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      3674: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      3677: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      3680: aastore
      3681: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      3684: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      3687: ldc           #194                // String Body1
      3689: new           #14                 // class net/minecraft/class_7179
      3692: dup
      3693: getstatic     #88                 // Field net/minecraft/class_7179$class_7183.field_37886:Lnet/minecraft/class_7179$class_7182;
      3696: iconst_1
      3697: anewarray     #55                 // class net/minecraft/class_7186
      3700: dup
      3701: iconst_0
      3702: new           #55                 // class net/minecraft/class_7186
      3705: dup
      3706: fconst_0
      3707: ldc           #197                // float 4.0f
      3709: ldc           #198                // float 6.0f
      3711: ldc           #199                // float 16.0f
      3713: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      3716: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      3719: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      3722: aastore
      3723: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      3726: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      3729: ldc           #201                // String Body2
      3731: new           #14                 // class net/minecraft/class_7179
      3734: dup
      3735: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      3738: iconst_1
      3739: anewarray     #55                 // class net/minecraft/class_7186
      3742: dup
      3743: iconst_0
      3744: new           #55                 // class net/minecraft/class_7186
      3747: dup
      3748: fconst_0
      3749: ldc           #202                // float 116.2474f
      3751: ldc           #203                // float -63.9688f
      3753: ldc           #204                // float -153.7998f
      3755: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      3758: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      3761: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      3764: aastore
      3765: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      3768: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      3771: ldc           #201                // String Body2
      3773: new           #14                 // class net/minecraft/class_7179
      3776: dup
      3777: getstatic     #88                 // Field net/minecraft/class_7179$class_7183.field_37886:Lnet/minecraft/class_7179$class_7182;
      3780: iconst_1
      3781: anewarray     #55                 // class net/minecraft/class_7186
      3784: dup
      3785: iconst_0
      3786: new           #55                 // class net/minecraft/class_7186
      3789: dup
      3790: fconst_0
      3791: fconst_0
      3792: fconst_0
      3793: ldc           #205                // float -1.0f
      3795: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      3798: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      3801: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      3804: aastore
      3805: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      3808: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      3811: ldc           #207                // String Body3
      3813: new           #14                 // class net/minecraft/class_7179
      3816: dup
      3817: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      3820: iconst_1
      3821: anewarray     #55                 // class net/minecraft/class_7186
      3824: dup
      3825: iconst_0
      3826: new           #55                 // class net/minecraft/class_7186
      3829: dup
      3830: fconst_0
      3831: fconst_0
      3832: fconst_0
      3833: fconst_0
      3834: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      3837: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      3840: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      3843: aastore
      3844: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      3847: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      3850: ldc           #207                // String Body3
      3852: new           #14                 // class net/minecraft/class_7179
      3855: dup
      3856: getstatic     #88                 // Field net/minecraft/class_7179$class_7183.field_37886:Lnet/minecraft/class_7179$class_7182;
      3859: iconst_1
      3860: anewarray     #55                 // class net/minecraft/class_7186
      3863: dup
      3864: iconst_0
      3865: new           #55                 // class net/minecraft/class_7186
      3868: dup
      3869: fconst_0
      3870: fconst_0
      3871: fconst_0
      3872: fconst_0
      3873: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      3876: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      3879: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      3882: aastore
      3883: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      3886: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      3889: ldc           #209                // String Body4
      3891: new           #14                 // class net/minecraft/class_7179
      3894: dup
      3895: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      3898: iconst_1
      3899: anewarray     #55                 // class net/minecraft/class_7186
      3902: dup
      3903: iconst_0
      3904: new           #55                 // class net/minecraft/class_7186
      3907: dup
      3908: fconst_0
      3909: fconst_0
      3910: fconst_0
      3911: fconst_0
      3912: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      3915: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      3918: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      3921: aastore
      3922: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      3925: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      3928: ldc           #96                 // String Rattle
      3930: new           #14                 // class net/minecraft/class_7179
      3933: dup
      3934: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      3937: iconst_1
      3938: anewarray     #55                 // class net/minecraft/class_7186
      3941: dup
      3942: iconst_0
      3943: new           #55                 // class net/minecraft/class_7186
      3946: dup
      3947: fconst_0
      3948: ldc           #210                // float 31.0686f
      3950: ldc           #211                // float 29.9119f
      3952: ldc           #212                // float -23.7553f
      3954: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      3957: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      3960: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      3963: aastore
      3964: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      3967: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      3970: ldc           #96                 // String Rattle
      3972: new           #14                 // class net/minecraft/class_7179
      3975: dup
      3976: getstatic     #88                 // Field net/minecraft/class_7179$class_7183.field_37886:Lnet/minecraft/class_7179$class_7182;
      3979: iconst_1
      3980: anewarray     #55                 // class net/minecraft/class_7186
      3983: dup
      3984: iconst_0
      3985: new           #55                 // class net/minecraft/class_7186
      3988: dup
      3989: fconst_0
      3990: fconst_0
      3991: fconst_0
      3992: fconst_0
      3993: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      3996: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      3999: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      4002: aastore
      4003: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      4006: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      4009: ldc           #99                 // String Neck3
      4011: new           #14                 // class net/minecraft/class_7179
      4014: dup
      4015: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      4018: iconst_1
      4019: anewarray     #55                 // class net/minecraft/class_7186
      4022: dup
      4023: iconst_0
      4024: new           #55                 // class net/minecraft/class_7186
      4027: dup
      4028: fconst_0
      4029: fconst_0
      4030: fconst_0
      4031: fconst_0
      4032: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      4035: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      4038: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      4041: aastore
      4042: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      4045: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      4048: ldc           #107                // String Body6
      4050: new           #14                 // class net/minecraft/class_7179
      4053: dup
      4054: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      4057: iconst_1
      4058: anewarray     #55                 // class net/minecraft/class_7186
      4061: dup
      4062: iconst_0
      4063: new           #55                 // class net/minecraft/class_7186
      4066: dup
      4067: fconst_0
      4068: fconst_0
      4069: fconst_0
      4070: fconst_0
      4071: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      4074: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      4077: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      4080: aastore
      4081: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      4084: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      4087: ldc           #113                // String Body7
      4089: new           #14                 // class net/minecraft/class_7179
      4092: dup
      4093: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      4096: iconst_1
      4097: anewarray     #55                 // class net/minecraft/class_7186
      4100: dup
      4101: iconst_0
      4102: new           #55                 // class net/minecraft/class_7186
      4105: dup
      4106: fconst_0
      4107: fconst_0
      4108: fconst_0
      4109: fconst_0
      4110: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      4113: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      4116: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      4119: aastore
      4120: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      4123: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      4126: ldc           #119                // String Body8
      4128: new           #14                 // class net/minecraft/class_7179
      4131: dup
      4132: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      4135: iconst_1
      4136: anewarray     #55                 // class net/minecraft/class_7186
      4139: dup
      4140: iconst_0
      4141: new           #55                 // class net/minecraft/class_7186
      4144: dup
      4145: fconst_0
      4146: fconst_0
      4147: ldc           #213                // float -15.0f
      4149: fconst_0
      4150: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      4153: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      4156: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      4159: aastore
      4160: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      4163: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      4166: ldc           #125                // String Body9
      4168: new           #14                 // class net/minecraft/class_7179
      4171: dup
      4172: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      4175: iconst_1
      4176: anewarray     #55                 // class net/minecraft/class_7186
      4179: dup
      4180: iconst_0
      4181: new           #55                 // class net/minecraft/class_7186
      4184: dup
      4185: fconst_0
      4186: fconst_0
      4187: ldc           #214                // float 72.5f
      4189: fconst_0
      4190: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      4193: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      4196: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      4199: aastore
      4200: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      4203: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      4206: ldc           #131                // String Body10
      4208: new           #14                 // class net/minecraft/class_7179
      4211: dup
      4212: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      4215: iconst_1
      4216: anewarray     #55                 // class net/minecraft/class_7186
      4219: dup
      4220: iconst_0
      4221: new           #55                 // class net/minecraft/class_7186
      4224: dup
      4225: fconst_0
      4226: fconst_0
      4227: ldc           #139                // float 50.0f
      4229: fconst_0
      4230: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      4233: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      4236: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      4239: aastore
      4240: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      4243: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      4246: ldc           #133                // String Tail4
      4248: new           #14                 // class net/minecraft/class_7179
      4251: dup
      4252: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      4255: iconst_1
      4256: anewarray     #55                 // class net/minecraft/class_7186
      4259: dup
      4260: iconst_0
      4261: new           #55                 // class net/minecraft/class_7186
      4264: dup
      4265: fconst_0
      4266: fconst_0
      4267: ldc           #129                // float 42.5f
      4269: fconst_0
      4270: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      4273: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      4276: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      4279: aastore
      4280: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      4283: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      4286: ldc           #137                // String Tail5
      4288: new           #14                 // class net/minecraft/class_7179
      4291: dup
      4292: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      4295: iconst_1
      4296: anewarray     #55                 // class net/minecraft/class_7186
      4299: dup
      4300: iconst_0
      4301: new           #55                 // class net/minecraft/class_7186
      4304: dup
      4305: fconst_0
      4306: fconst_0
      4307: ldc           #97                 // float 47.5f
      4309: fconst_0
      4310: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      4313: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      4316: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      4319: aastore
      4320: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      4323: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      4326: ldc           #142                // String Tail6
      4328: new           #14                 // class net/minecraft/class_7179
      4331: dup
      4332: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      4335: iconst_1
      4336: anewarray     #55                 // class net/minecraft/class_7186
      4339: dup
      4340: iconst_0
      4341: new           #55                 // class net/minecraft/class_7186
      4344: dup
      4345: fconst_0
      4346: ldc           #215                // float 35.9802f
      4348: ldc           #216                // float 6.1147f
      4350: ldc           #217                // float -16.0205f
      4352: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      4355: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      4358: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      4361: aastore
      4362: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      4365: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      4368: ldc           #219                // String Face3
      4370: new           #14                 // class net/minecraft/class_7179
      4373: dup
      4374: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      4377: iconst_1
      4378: anewarray     #55                 // class net/minecraft/class_7186
      4381: dup
      4382: iconst_0
      4383: new           #55                 // class net/minecraft/class_7186
      4386: dup
      4387: fconst_0
      4388: fconst_0
      4389: fconst_0
      4390: fconst_0
      4391: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      4394: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      4397: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      4400: aastore
      4401: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      4404: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      4407: ldc           #221                // String Face22
      4409: new           #14                 // class net/minecraft/class_7179
      4412: dup
      4413: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      4416: iconst_1
      4417: anewarray     #55                 // class net/minecraft/class_7186
      4420: dup
      4421: iconst_0
      4422: new           #55                 // class net/minecraft/class_7186
      4425: dup
      4426: fconst_0
      4427: fconst_0
      4428: fconst_0
      4429: fconst_0
      4430: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      4433: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      4436: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      4439: aastore
      4440: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      4443: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      4446: ldc           #223                // String Tongue
      4448: new           #14                 // class net/minecraft/class_7179
      4451: dup
      4452: getstatic     #88                 // Field net/minecraft/class_7179$class_7183.field_37886:Lnet/minecraft/class_7179$class_7182;
      4455: iconst_1
      4456: anewarray     #55                 // class net/minecraft/class_7186
      4459: dup
      4460: iconst_0
      4461: new           #55                 // class net/minecraft/class_7186
      4464: dup
      4465: fconst_0
      4466: fconst_0
      4467: fconst_0
      4468: fconst_0
      4469: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      4472: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      4475: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      4478: aastore
      4479: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      4482: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      4485: invokevirtual #156                // Method net/minecraft/class_7184$class_7185.method_41821:()Lnet/minecraft/class_7184;
      4488: putstatic     #225                // Field ANIM_RATTLESNAKE_COILED_UP2:Lnet/minecraft/class_7184;
      4491: fconst_0
      4492: invokestatic  #43                 // Method net/minecraft/class_7184$class_7185.method_41818:(F)Lnet/minecraft/class_7184$class_7185;
      4495: ldc           #49                 // String RattleSnake
      4497: new           #14                 // class net/minecraft/class_7179
      4500: dup
      4501: getstatic     #88                 // Field net/minecraft/class_7179$class_7183.field_37886:Lnet/minecraft/class_7179$class_7182;
      4504: iconst_1
      4505: anewarray     #55                 // class net/minecraft/class_7186
      4508: dup
      4509: iconst_0
      4510: new           #55                 // class net/minecraft/class_7186
      4513: dup
      4514: fconst_0
      4515: ldc           #226                // float -4.0f
      4517: fconst_0
      4518: ldc           #227                // float -1.5f
      4520: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      4523: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      4526: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      4529: aastore
      4530: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      4533: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      4536: ldc           #81                 // String Head
      4538: new           #14                 // class net/minecraft/class_7179
      4541: dup
      4542: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      4545: iconst_1
      4546: anewarray     #55                 // class net/minecraft/class_7186
      4549: dup
      4550: iconst_0
      4551: new           #55                 // class net/minecraft/class_7186
      4554: dup
      4555: fconst_0
      4556: ldc           #162                // float -17.5f
      4558: fconst_0
      4559: fconst_0
      4560: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      4563: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      4566: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      4569: aastore
      4570: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      4573: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      4576: ldc           #81                 // String Head
      4578: new           #14                 // class net/minecraft/class_7179
      4581: dup
      4582: getstatic     #88                 // Field net/minecraft/class_7179$class_7183.field_37886:Lnet/minecraft/class_7179$class_7182;
      4585: iconst_1
      4586: anewarray     #55                 // class net/minecraft/class_7186
      4589: dup
      4590: iconst_0
      4591: new           #55                 // class net/minecraft/class_7186
      4594: dup
      4595: fconst_0
      4596: ldc           #182                // float 5.5f
      4598: ldc           #183                // float 6.75f
      4600: ldc           #184                // float 18.25f
      4602: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      4605: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      4608: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      4611: aastore
      4612: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      4615: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      4618: ldc           #93                 // String Neck1
      4620: new           #14                 // class net/minecraft/class_7179
      4623: dup
      4624: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      4627: iconst_1
      4628: anewarray     #55                 // class net/minecraft/class_7186
      4631: dup
      4632: iconst_0
      4633: new           #55                 // class net/minecraft/class_7186
      4636: dup
      4637: fconst_0
      4638: ldc           #71                 // float 20.0f
      4640: ldc           #103                // float 30.0f
      4642: fconst_0
      4643: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      4646: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      4649: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      4652: aastore
      4653: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      4656: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      4659: ldc           #93                 // String Neck1
      4661: new           #14                 // class net/minecraft/class_7179
      4664: dup
      4665: getstatic     #88                 // Field net/minecraft/class_7179$class_7183.field_37886:Lnet/minecraft/class_7179$class_7182;
      4668: iconst_1
      4669: anewarray     #55                 // class net/minecraft/class_7186
      4672: dup
      4673: iconst_0
      4674: new           #55                 // class net/minecraft/class_7186
      4677: dup
      4678: fconst_0
      4679: ldc           #185                // float 7.0f
      4681: ldc           #186                // float 8.0f
      4683: ldc           #164                // float 17.5f
      4685: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      4688: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      4691: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      4694: aastore
      4695: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      4698: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      4701: ldc           #188                // String Neck2
      4703: new           #14                 // class net/minecraft/class_7179
      4706: dup
      4707: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      4710: iconst_1
      4711: anewarray     #55                 // class net/minecraft/class_7186
      4714: dup
      4715: iconst_0
      4716: new           #55                 // class net/minecraft/class_7186
      4719: dup
      4720: fconst_0
      4721: ldc           #189                // float -6.2607f
      4723: ldc           #190                // float -42.4066f
      4725: ldc           #191                // float 2.3137f
      4727: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      4730: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      4733: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      4736: aastore
      4737: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      4740: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      4743: ldc           #188                // String Neck2
      4745: new           #14                 // class net/minecraft/class_7179
      4748: dup
      4749: getstatic     #88                 // Field net/minecraft/class_7179$class_7183.field_37886:Lnet/minecraft/class_7179$class_7182;
      4752: iconst_1
      4753: anewarray     #55                 // class net/minecraft/class_7186
      4756: dup
      4757: iconst_0
      4758: new           #55                 // class net/minecraft/class_7186
      4761: dup
      4762: fconst_0
      4763: ldc           #192                // float 6.5f
      4765: ldc           #186                // float 8.0f
      4767: ldc           #164                // float 17.5f
      4769: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      4772: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      4775: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      4778: aastore
      4779: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      4782: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      4785: ldc           #194                // String Body1
      4787: new           #14                 // class net/minecraft/class_7179
      4790: dup
      4791: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      4794: iconst_1
      4795: anewarray     #55                 // class net/minecraft/class_7186
      4798: dup
      4799: iconst_0
      4800: new           #55                 // class net/minecraft/class_7186
      4803: dup
      4804: fconst_0
      4805: ldc           #195                // float 155.0f
      4807: ldc           #159                // float -5.0f
      4809: ldc           #196                // float -180.0f
      4811: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      4814: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      4817: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      4820: aastore
      4821: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      4824: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      4827: ldc           #194                // String Body1
      4829: new           #14                 // class net/minecraft/class_7179
      4832: dup
      4833: getstatic     #88                 // Field net/minecraft/class_7179$class_7183.field_37886:Lnet/minecraft/class_7179$class_7182;
      4836: iconst_1
      4837: anewarray     #55                 // class net/minecraft/class_7186
      4840: dup
      4841: iconst_0
      4842: new           #55                 // class net/minecraft/class_7186
      4845: dup
      4846: fconst_0
      4847: ldc           #197                // float 4.0f
      4849: ldc           #198                // float 6.0f
      4851: ldc           #199                // float 16.0f
      4853: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      4856: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      4859: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      4862: aastore
      4863: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      4866: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      4869: ldc           #201                // String Body2
      4871: new           #14                 // class net/minecraft/class_7179
      4874: dup
      4875: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      4878: iconst_1
      4879: anewarray     #55                 // class net/minecraft/class_7186
      4882: dup
      4883: iconst_0
      4884: new           #55                 // class net/minecraft/class_7186
      4887: dup
      4888: fconst_0
      4889: ldc           #202                // float 116.2474f
      4891: ldc           #203                // float -63.9688f
      4893: ldc           #204                // float -153.7998f
      4895: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      4898: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      4901: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      4904: aastore
      4905: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      4908: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      4911: ldc           #201                // String Body2
      4913: new           #14                 // class net/minecraft/class_7179
      4916: dup
      4917: getstatic     #88                 // Field net/minecraft/class_7179$class_7183.field_37886:Lnet/minecraft/class_7179$class_7182;
      4920: iconst_1
      4921: anewarray     #55                 // class net/minecraft/class_7186
      4924: dup
      4925: iconst_0
      4926: new           #55                 // class net/minecraft/class_7186
      4929: dup
      4930: fconst_0
      4931: fconst_0
      4932: fconst_0
      4933: ldc           #205                // float -1.0f
      4935: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      4938: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      4941: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      4944: aastore
      4945: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      4948: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      4951: ldc           #207                // String Body3
      4953: new           #14                 // class net/minecraft/class_7179
      4956: dup
      4957: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      4960: iconst_1
      4961: anewarray     #55                 // class net/minecraft/class_7186
      4964: dup
      4965: iconst_0
      4966: new           #55                 // class net/minecraft/class_7186
      4969: dup
      4970: fconst_0
      4971: fconst_0
      4972: fconst_0
      4973: fconst_0
      4974: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      4977: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      4980: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      4983: aastore
      4984: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      4987: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      4990: ldc           #207                // String Body3
      4992: new           #14                 // class net/minecraft/class_7179
      4995: dup
      4996: getstatic     #88                 // Field net/minecraft/class_7179$class_7183.field_37886:Lnet/minecraft/class_7179$class_7182;
      4999: iconst_1
      5000: anewarray     #55                 // class net/minecraft/class_7186
      5003: dup
      5004: iconst_0
      5005: new           #55                 // class net/minecraft/class_7186
      5008: dup
      5009: fconst_0
      5010: fconst_0
      5011: fconst_0
      5012: fconst_0
      5013: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      5016: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      5019: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      5022: aastore
      5023: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      5026: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      5029: ldc           #209                // String Body4
      5031: new           #14                 // class net/minecraft/class_7179
      5034: dup
      5035: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      5038: iconst_1
      5039: anewarray     #55                 // class net/minecraft/class_7186
      5042: dup
      5043: iconst_0
      5044: new           #55                 // class net/minecraft/class_7186
      5047: dup
      5048: fconst_0
      5049: fconst_0
      5050: fconst_0
      5051: fconst_0
      5052: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      5055: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      5058: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      5061: aastore
      5062: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      5065: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      5068: ldc           #96                 // String Rattle
      5070: new           #14                 // class net/minecraft/class_7179
      5073: dup
      5074: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      5077: iconst_1
      5078: anewarray     #55                 // class net/minecraft/class_7186
      5081: dup
      5082: iconst_0
      5083: new           #55                 // class net/minecraft/class_7186
      5086: dup
      5087: fconst_0
      5088: ldc           #210                // float 31.0686f
      5090: ldc           #211                // float 29.9119f
      5092: ldc           #212                // float -23.7553f
      5094: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      5097: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      5100: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      5103: aastore
      5104: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      5107: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      5110: ldc           #96                 // String Rattle
      5112: new           #14                 // class net/minecraft/class_7179
      5115: dup
      5116: getstatic     #88                 // Field net/minecraft/class_7179$class_7183.field_37886:Lnet/minecraft/class_7179$class_7182;
      5119: iconst_1
      5120: anewarray     #55                 // class net/minecraft/class_7186
      5123: dup
      5124: iconst_0
      5125: new           #55                 // class net/minecraft/class_7186
      5128: dup
      5129: fconst_0
      5130: fconst_0
      5131: fconst_0
      5132: fconst_0
      5133: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      5136: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      5139: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      5142: aastore
      5143: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      5146: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      5149: ldc           #99                 // String Neck3
      5151: new           #14                 // class net/minecraft/class_7179
      5154: dup
      5155: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      5158: iconst_1
      5159: anewarray     #55                 // class net/minecraft/class_7186
      5162: dup
      5163: iconst_0
      5164: new           #55                 // class net/minecraft/class_7186
      5167: dup
      5168: fconst_0
      5169: fconst_0
      5170: fconst_0
      5171: fconst_0
      5172: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      5175: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      5178: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      5181: aastore
      5182: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      5185: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      5188: ldc           #107                // String Body6
      5190: new           #14                 // class net/minecraft/class_7179
      5193: dup
      5194: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      5197: iconst_1
      5198: anewarray     #55                 // class net/minecraft/class_7186
      5201: dup
      5202: iconst_0
      5203: new           #55                 // class net/minecraft/class_7186
      5206: dup
      5207: fconst_0
      5208: fconst_0
      5209: fconst_0
      5210: fconst_0
      5211: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      5214: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      5217: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      5220: aastore
      5221: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      5224: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      5227: ldc           #113                // String Body7
      5229: new           #14                 // class net/minecraft/class_7179
      5232: dup
      5233: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      5236: iconst_1
      5237: anewarray     #55                 // class net/minecraft/class_7186
      5240: dup
      5241: iconst_0
      5242: new           #55                 // class net/minecraft/class_7186
      5245: dup
      5246: fconst_0
      5247: fconst_0
      5248: fconst_0
      5249: fconst_0
      5250: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      5253: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      5256: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      5259: aastore
      5260: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      5263: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      5266: ldc           #119                // String Body8
      5268: new           #14                 // class net/minecraft/class_7179
      5271: dup
      5272: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      5275: iconst_1
      5276: anewarray     #55                 // class net/minecraft/class_7186
      5279: dup
      5280: iconst_0
      5281: new           #55                 // class net/minecraft/class_7186
      5284: dup
      5285: fconst_0
      5286: fconst_0
      5287: ldc           #213                // float -15.0f
      5289: fconst_0
      5290: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      5293: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      5296: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      5299: aastore
      5300: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      5303: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      5306: ldc           #125                // String Body9
      5308: new           #14                 // class net/minecraft/class_7179
      5311: dup
      5312: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      5315: iconst_1
      5316: anewarray     #55                 // class net/minecraft/class_7186
      5319: dup
      5320: iconst_0
      5321: new           #55                 // class net/minecraft/class_7186
      5324: dup
      5325: fconst_0
      5326: fconst_0
      5327: ldc           #214                // float 72.5f
      5329: fconst_0
      5330: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      5333: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      5336: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      5339: aastore
      5340: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      5343: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      5346: ldc           #131                // String Body10
      5348: new           #14                 // class net/minecraft/class_7179
      5351: dup
      5352: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      5355: iconst_1
      5356: anewarray     #55                 // class net/minecraft/class_7186
      5359: dup
      5360: iconst_0
      5361: new           #55                 // class net/minecraft/class_7186
      5364: dup
      5365: fconst_0
      5366: fconst_0
      5367: ldc           #139                // float 50.0f
      5369: fconst_0
      5370: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      5373: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      5376: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      5379: aastore
      5380: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      5383: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      5386: ldc           #133                // String Tail4
      5388: new           #14                 // class net/minecraft/class_7179
      5391: dup
      5392: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      5395: iconst_1
      5396: anewarray     #55                 // class net/minecraft/class_7186
      5399: dup
      5400: iconst_0
      5401: new           #55                 // class net/minecraft/class_7186
      5404: dup
      5405: fconst_0
      5406: fconst_0
      5407: ldc           #129                // float 42.5f
      5409: fconst_0
      5410: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      5413: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      5416: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      5419: aastore
      5420: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      5423: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      5426: ldc           #137                // String Tail5
      5428: new           #14                 // class net/minecraft/class_7179
      5431: dup
      5432: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      5435: iconst_1
      5436: anewarray     #55                 // class net/minecraft/class_7186
      5439: dup
      5440: iconst_0
      5441: new           #55                 // class net/minecraft/class_7186
      5444: dup
      5445: fconst_0
      5446: fconst_0
      5447: ldc           #97                 // float 47.5f
      5449: fconst_0
      5450: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      5453: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      5456: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      5459: aastore
      5460: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      5463: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      5466: ldc           #142                // String Tail6
      5468: new           #14                 // class net/minecraft/class_7179
      5471: dup
      5472: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      5475: iconst_1
      5476: anewarray     #55                 // class net/minecraft/class_7186
      5479: dup
      5480: iconst_0
      5481: new           #55                 // class net/minecraft/class_7186
      5484: dup
      5485: fconst_0
      5486: ldc           #215                // float 35.9802f
      5488: ldc           #216                // float 6.1147f
      5490: ldc           #217                // float -16.0205f
      5492: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      5495: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      5498: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      5501: aastore
      5502: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      5505: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      5508: ldc           #219                // String Face3
      5510: new           #14                 // class net/minecraft/class_7179
      5513: dup
      5514: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      5517: iconst_1
      5518: anewarray     #55                 // class net/minecraft/class_7186
      5521: dup
      5522: iconst_0
      5523: new           #55                 // class net/minecraft/class_7186
      5526: dup
      5527: fconst_0
      5528: fconst_0
      5529: fconst_0
      5530: fconst_0
      5531: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      5534: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      5537: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      5540: aastore
      5541: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      5544: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      5547: ldc           #221                // String Face22
      5549: new           #14                 // class net/minecraft/class_7179
      5552: dup
      5553: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      5556: iconst_1
      5557: anewarray     #55                 // class net/minecraft/class_7186
      5560: dup
      5561: iconst_0
      5562: new           #55                 // class net/minecraft/class_7186
      5565: dup
      5566: fconst_0
      5567: fconst_0
      5568: fconst_0
      5569: fconst_0
      5570: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      5573: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      5576: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      5579: aastore
      5580: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      5583: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      5586: invokevirtual #156                // Method net/minecraft/class_7184$class_7185.method_41821:()Lnet/minecraft/class_7184;
      5589: putstatic     #229                // Field ANIM_RATTLESNAKE_COILED_UP:Lnet/minecraft/class_7184;
      5592: fconst_1
      5593: invokestatic  #43                 // Method net/minecraft/class_7184$class_7185.method_41818:(F)Lnet/minecraft/class_7184$class_7185;
      5596: ldc           #81                 // String Head
      5598: new           #14                 // class net/minecraft/class_7179
      5601: dup
      5602: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      5605: iconst_3
      5606: anewarray     #55                 // class net/minecraft/class_7186
      5609: dup
      5610: iconst_0
      5611: new           #55                 // class net/minecraft/class_7186
      5614: dup
      5615: fconst_0
      5616: ldc           #162                // float -17.5f
      5618: fconst_0
      5619: fconst_0
      5620: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      5623: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      5626: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      5629: aastore
      5630: dup
      5631: iconst_1
      5632: new           #55                 // class net/minecraft/class_7186
      5635: dup
      5636: ldc           #230                // float 0.0833f
      5638: fconst_0
      5639: fconst_0
      5640: fconst_0
      5641: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      5644: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      5647: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      5650: aastore
      5651: dup
      5652: iconst_2
      5653: new           #55                 // class net/minecraft/class_7186
      5656: dup
      5657: fconst_1
      5658: fconst_0
      5659: fconst_0
      5660: fconst_0
      5661: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      5664: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      5667: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      5670: aastore
      5671: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      5674: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      5677: ldc           #81                 // String Head
      5679: new           #14                 // class net/minecraft/class_7179
      5682: dup
      5683: getstatic     #88                 // Field net/minecraft/class_7179$class_7183.field_37886:Lnet/minecraft/class_7179$class_7182;
      5686: iconst_3
      5687: anewarray     #55                 // class net/minecraft/class_7186
      5690: dup
      5691: iconst_0
      5692: new           #55                 // class net/minecraft/class_7186
      5695: dup
      5696: fconst_0
      5697: ldc           #182                // float 5.5f
      5699: ldc           #183                // float 6.75f
      5701: ldc           #184                // float 18.25f
      5703: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      5706: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      5709: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      5712: aastore
      5713: dup
      5714: iconst_1
      5715: new           #55                 // class net/minecraft/class_7186
      5718: dup
      5719: ldc           #230                // float 0.0833f
      5721: ldc           #227                // float -1.5f
      5723: ldc           #231                // float 7.25f
      5725: ldc           #101                // float 0.75f
      5727: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      5730: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      5733: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      5736: aastore
      5737: dup
      5738: iconst_2
      5739: new           #55                 // class net/minecraft/class_7186
      5742: dup
      5743: fconst_1
      5744: ldc           #227                // float -1.5f
      5746: ldc           #231                // float 7.25f
      5748: ldc           #101                // float 0.75f
      5750: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      5753: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      5756: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      5759: aastore
      5760: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      5763: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      5766: ldc           #93                 // String Neck1
      5768: new           #14                 // class net/minecraft/class_7179
      5771: dup
      5772: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      5775: iconst_3
      5776: anewarray     #55                 // class net/minecraft/class_7186
      5779: dup
      5780: iconst_0
      5781: new           #55                 // class net/minecraft/class_7186
      5784: dup
      5785: fconst_0
      5786: ldc           #71                 // float 20.0f
      5788: ldc           #103                // float 30.0f
      5790: fconst_0
      5791: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      5794: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      5797: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      5800: aastore
      5801: dup
      5802: iconst_1
      5803: new           #55                 // class net/minecraft/class_7186
      5806: dup
      5807: ldc           #230                // float 0.0833f
      5809: fconst_0
      5810: fconst_0
      5811: fconst_0
      5812: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      5815: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      5818: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      5821: aastore
      5822: dup
      5823: iconst_2
      5824: new           #55                 // class net/minecraft/class_7186
      5827: dup
      5828: fconst_1
      5829: fconst_0
      5830: fconst_0
      5831: fconst_0
      5832: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      5835: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      5838: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      5841: aastore
      5842: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      5845: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      5848: ldc           #93                 // String Neck1
      5850: new           #14                 // class net/minecraft/class_7179
      5853: dup
      5854: getstatic     #88                 // Field net/minecraft/class_7179$class_7183.field_37886:Lnet/minecraft/class_7179$class_7182;
      5857: iconst_3
      5858: anewarray     #55                 // class net/minecraft/class_7186
      5861: dup
      5862: iconst_0
      5863: new           #55                 // class net/minecraft/class_7186
      5866: dup
      5867: fconst_0
      5868: ldc           #185                // float 7.0f
      5870: ldc           #186                // float 8.0f
      5872: ldc           #164                // float 17.5f
      5874: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      5877: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      5880: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      5883: aastore
      5884: dup
      5885: iconst_1
      5886: new           #55                 // class net/minecraft/class_7186
      5889: dup
      5890: ldc           #230                // float 0.0833f
      5892: ldc           #232                // float -1.75f
      5894: ldc           #231                // float 7.25f
      5896: ldc           #101                // float 0.75f
      5898: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      5901: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      5904: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      5907: aastore
      5908: dup
      5909: iconst_2
      5910: new           #55                 // class net/minecraft/class_7186
      5913: dup
      5914: fconst_1
      5915: ldc           #232                // float -1.75f
      5917: ldc           #231                // float 7.25f
      5919: ldc           #101                // float 0.75f
      5921: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      5924: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      5927: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      5930: aastore
      5931: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      5934: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      5937: ldc           #188                // String Neck2
      5939: new           #14                 // class net/minecraft/class_7179
      5942: dup
      5943: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      5946: iconst_3
      5947: anewarray     #55                 // class net/minecraft/class_7186
      5950: dup
      5951: iconst_0
      5952: new           #55                 // class net/minecraft/class_7186
      5955: dup
      5956: fconst_0
      5957: ldc           #189                // float -6.2607f
      5959: ldc           #190                // float -42.4066f
      5961: ldc           #191                // float 2.3137f
      5963: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      5966: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      5969: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      5972: aastore
      5973: dup
      5974: iconst_1
      5975: new           #55                 // class net/minecraft/class_7186
      5978: dup
      5979: ldc           #230                // float 0.0833f
      5981: ldc           #233                // float -12.5f
      5983: fconst_0
      5984: fconst_0
      5985: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      5988: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      5991: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      5994: aastore
      5995: dup
      5996: iconst_2
      5997: new           #55                 // class net/minecraft/class_7186
      6000: dup
      6001: fconst_1
      6002: ldc           #233                // float -12.5f
      6004: fconst_0
      6005: fconst_0
      6006: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      6009: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6012: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6015: aastore
      6016: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      6019: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      6022: ldc           #188                // String Neck2
      6024: new           #14                 // class net/minecraft/class_7179
      6027: dup
      6028: getstatic     #88                 // Field net/minecraft/class_7179$class_7183.field_37886:Lnet/minecraft/class_7179$class_7182;
      6031: iconst_3
      6032: anewarray     #55                 // class net/minecraft/class_7186
      6035: dup
      6036: iconst_0
      6037: new           #55                 // class net/minecraft/class_7186
      6040: dup
      6041: fconst_0
      6042: ldc           #192                // float 6.5f
      6044: ldc           #186                // float 8.0f
      6046: ldc           #164                // float 17.5f
      6048: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      6051: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6054: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6057: aastore
      6058: dup
      6059: iconst_1
      6060: new           #55                 // class net/minecraft/class_7186
      6063: dup
      6064: ldc           #230                // float 0.0833f
      6066: ldc           #227                // float -1.5f
      6068: ldc           #183                // float 6.75f
      6070: ldc           #101                // float 0.75f
      6072: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      6075: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6078: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6081: aastore
      6082: dup
      6083: iconst_2
      6084: new           #55                 // class net/minecraft/class_7186
      6087: dup
      6088: fconst_1
      6089: ldc           #227                // float -1.5f
      6091: ldc           #183                // float 6.75f
      6093: ldc           #101                // float 0.75f
      6095: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      6098: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6101: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6104: aastore
      6105: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      6108: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      6111: ldc           #194                // String Body1
      6113: new           #14                 // class net/minecraft/class_7179
      6116: dup
      6117: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      6120: iconst_3
      6121: anewarray     #55                 // class net/minecraft/class_7186
      6124: dup
      6125: iconst_0
      6126: new           #55                 // class net/minecraft/class_7186
      6129: dup
      6130: fconst_0
      6131: ldc           #195                // float 155.0f
      6133: ldc           #159                // float -5.0f
      6135: ldc           #196                // float -180.0f
      6137: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      6140: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6143: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6146: aastore
      6147: dup
      6148: iconst_1
      6149: new           #55                 // class net/minecraft/class_7186
      6152: dup
      6153: ldc           #230                // float 0.0833f
      6155: ldc           #233                // float -12.5f
      6157: fconst_0
      6158: fconst_0
      6159: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      6162: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6165: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6168: aastore
      6169: dup
      6170: iconst_2
      6171: new           #55                 // class net/minecraft/class_7186
      6174: dup
      6175: fconst_1
      6176: ldc           #233                // float -12.5f
      6178: fconst_0
      6179: fconst_0
      6180: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      6183: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6186: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6189: aastore
      6190: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      6193: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      6196: ldc           #194                // String Body1
      6198: new           #14                 // class net/minecraft/class_7179
      6201: dup
      6202: getstatic     #88                 // Field net/minecraft/class_7179$class_7183.field_37886:Lnet/minecraft/class_7179$class_7182;
      6205: iconst_3
      6206: anewarray     #55                 // class net/minecraft/class_7186
      6209: dup
      6210: iconst_0
      6211: new           #55                 // class net/minecraft/class_7186
      6214: dup
      6215: fconst_0
      6216: ldc           #197                // float 4.0f
      6218: ldc           #198                // float 6.0f
      6220: ldc           #199                // float 16.0f
      6222: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      6225: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6228: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6231: aastore
      6232: dup
      6233: iconst_1
      6234: new           #55                 // class net/minecraft/class_7186
      6237: dup
      6238: ldc           #230                // float 0.0833f
      6240: ldc           #227                // float -1.5f
      6242: ldc           #182                // float 5.5f
      6244: ldc           #163                // float 0.25f
      6246: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      6249: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6252: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6255: aastore
      6256: dup
      6257: iconst_2
      6258: new           #55                 // class net/minecraft/class_7186
      6261: dup
      6262: fconst_1
      6263: ldc           #227                // float -1.5f
      6265: ldc           #182                // float 5.5f
      6267: ldc           #163                // float 0.25f
      6269: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      6272: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6275: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6278: aastore
      6279: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      6282: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      6285: ldc           #201                // String Body2
      6287: new           #14                 // class net/minecraft/class_7179
      6290: dup
      6291: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      6294: iconst_3
      6295: anewarray     #55                 // class net/minecraft/class_7186
      6298: dup
      6299: iconst_0
      6300: new           #55                 // class net/minecraft/class_7186
      6303: dup
      6304: fconst_0
      6305: ldc           #202                // float 116.2474f
      6307: ldc           #203                // float -63.9688f
      6309: ldc           #204                // float -153.7998f
      6311: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      6314: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6317: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6320: aastore
      6321: dup
      6322: iconst_1
      6323: new           #55                 // class net/minecraft/class_7186
      6326: dup
      6327: ldc           #230                // float 0.0833f
      6329: ldc           #213                // float -15.0f
      6331: fconst_0
      6332: fconst_0
      6333: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      6336: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6339: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6342: aastore
      6343: dup
      6344: iconst_2
      6345: new           #55                 // class net/minecraft/class_7186
      6348: dup
      6349: fconst_1
      6350: ldc           #213                // float -15.0f
      6352: fconst_0
      6353: fconst_0
      6354: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      6357: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6360: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6363: aastore
      6364: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      6367: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      6370: ldc           #201                // String Body2
      6372: new           #14                 // class net/minecraft/class_7179
      6375: dup
      6376: getstatic     #88                 // Field net/minecraft/class_7179$class_7183.field_37886:Lnet/minecraft/class_7179$class_7182;
      6379: iconst_3
      6380: anewarray     #55                 // class net/minecraft/class_7186
      6383: dup
      6384: iconst_0
      6385: new           #55                 // class net/minecraft/class_7186
      6388: dup
      6389: fconst_0
      6390: fconst_0
      6391: fconst_0
      6392: ldc           #205                // float -1.0f
      6394: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      6397: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6400: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6403: aastore
      6404: dup
      6405: iconst_1
      6406: new           #55                 // class net/minecraft/class_7186
      6409: dup
      6410: ldc           #230                // float 0.0833f
      6412: ldc           #227                // float -1.5f
      6414: fconst_2
      6415: fconst_0
      6416: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      6419: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6422: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6425: aastore
      6426: dup
      6427: iconst_2
      6428: new           #55                 // class net/minecraft/class_7186
      6431: dup
      6432: fconst_1
      6433: ldc           #227                // float -1.5f
      6435: fconst_2
      6436: fconst_0
      6437: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      6440: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6443: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6446: aastore
      6447: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      6450: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      6453: ldc           #207                // String Body3
      6455: new           #14                 // class net/minecraft/class_7179
      6458: dup
      6459: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      6462: iconst_3
      6463: anewarray     #55                 // class net/minecraft/class_7186
      6466: dup
      6467: iconst_0
      6468: new           #55                 // class net/minecraft/class_7186
      6471: dup
      6472: fconst_0
      6473: fconst_0
      6474: fconst_0
      6475: fconst_0
      6476: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      6479: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6482: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6485: aastore
      6486: dup
      6487: iconst_1
      6488: new           #55                 // class net/minecraft/class_7186
      6491: dup
      6492: ldc           #230                // float 0.0833f
      6494: ldc           #82                 // float -10.0f
      6496: ldc           #164                // float 17.5f
      6498: fconst_0
      6499: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      6502: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6505: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6508: aastore
      6509: dup
      6510: iconst_2
      6511: new           #55                 // class net/minecraft/class_7186
      6514: dup
      6515: fconst_1
      6516: ldc           #82                 // float -10.0f
      6518: ldc           #164                // float 17.5f
      6520: fconst_0
      6521: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      6524: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6527: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6530: aastore
      6531: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      6534: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      6537: ldc           #207                // String Body3
      6539: new           #14                 // class net/minecraft/class_7179
      6542: dup
      6543: getstatic     #88                 // Field net/minecraft/class_7179$class_7183.field_37886:Lnet/minecraft/class_7179$class_7182;
      6546: iconst_3
      6547: anewarray     #55                 // class net/minecraft/class_7186
      6550: dup
      6551: iconst_0
      6552: new           #55                 // class net/minecraft/class_7186
      6555: dup
      6556: fconst_0
      6557: fconst_0
      6558: fconst_0
      6559: fconst_0
      6560: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      6563: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6566: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6569: aastore
      6570: dup
      6571: iconst_1
      6572: new           #55                 // class net/minecraft/class_7186
      6575: dup
      6576: ldc           #230                // float 0.0833f
      6578: ldc           #227                // float -1.5f
      6580: fconst_1
      6581: fconst_0
      6582: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      6585: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6588: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6591: aastore
      6592: dup
      6593: iconst_2
      6594: new           #55                 // class net/minecraft/class_7186
      6597: dup
      6598: fconst_1
      6599: ldc           #227                // float -1.5f
      6601: fconst_1
      6602: fconst_0
      6603: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      6606: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6609: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6612: aastore
      6613: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      6616: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      6619: ldc           #209                // String Body4
      6621: new           #14                 // class net/minecraft/class_7179
      6624: dup
      6625: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      6628: iconst_3
      6629: anewarray     #55                 // class net/minecraft/class_7186
      6632: dup
      6633: iconst_0
      6634: new           #55                 // class net/minecraft/class_7186
      6637: dup
      6638: fconst_0
      6639: fconst_0
      6640: fconst_0
      6641: fconst_0
      6642: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      6645: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6648: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6651: aastore
      6652: dup
      6653: iconst_1
      6654: new           #55                 // class net/minecraft/class_7186
      6657: dup
      6658: ldc           #230                // float 0.0833f
      6660: fconst_0
      6661: fconst_0
      6662: fconst_0
      6663: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      6666: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6669: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6672: aastore
      6673: dup
      6674: iconst_2
      6675: new           #55                 // class net/minecraft/class_7186
      6678: dup
      6679: fconst_1
      6680: fconst_0
      6681: fconst_0
      6682: fconst_0
      6683: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      6686: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6689: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6692: aastore
      6693: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      6696: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      6699: ldc           #96                 // String Rattle
      6701: new           #14                 // class net/minecraft/class_7179
      6704: dup
      6705: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      6708: iconst_3
      6709: anewarray     #55                 // class net/minecraft/class_7186
      6712: dup
      6713: iconst_0
      6714: new           #55                 // class net/minecraft/class_7186
      6717: dup
      6718: fconst_0
      6719: ldc           #210                // float 31.0686f
      6721: ldc           #211                // float 29.9119f
      6723: ldc           #212                // float -23.7553f
      6725: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      6728: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6731: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6734: aastore
      6735: dup
      6736: iconst_1
      6737: new           #55                 // class net/minecraft/class_7186
      6740: dup
      6741: ldc           #230                // float 0.0833f
      6743: ldc           #210                // float 31.0686f
      6745: ldc           #211                // float 29.9119f
      6747: ldc           #212                // float -23.7553f
      6749: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      6752: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6755: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6758: aastore
      6759: dup
      6760: iconst_2
      6761: new           #55                 // class net/minecraft/class_7186
      6764: dup
      6765: fconst_1
      6766: ldc           #210                // float 31.0686f
      6768: ldc           #211                // float 29.9119f
      6770: ldc           #212                // float -23.7553f
      6772: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      6775: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6778: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6781: aastore
      6782: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      6785: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      6788: ldc           #96                 // String Rattle
      6790: new           #14                 // class net/minecraft/class_7179
      6793: dup
      6794: getstatic     #88                 // Field net/minecraft/class_7179$class_7183.field_37886:Lnet/minecraft/class_7179$class_7182;
      6797: iconst_3
      6798: anewarray     #55                 // class net/minecraft/class_7186
      6801: dup
      6802: iconst_0
      6803: new           #55                 // class net/minecraft/class_7186
      6806: dup
      6807: fconst_0
      6808: fconst_0
      6809: fconst_0
      6810: fconst_0
      6811: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      6814: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6817: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6820: aastore
      6821: dup
      6822: iconst_1
      6823: new           #55                 // class net/minecraft/class_7186
      6826: dup
      6827: ldc           #230                // float 0.0833f
      6829: fconst_0
      6830: fconst_0
      6831: fconst_0
      6832: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      6835: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6838: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6841: aastore
      6842: dup
      6843: iconst_2
      6844: new           #55                 // class net/minecraft/class_7186
      6847: dup
      6848: fconst_1
      6849: fconst_0
      6850: fconst_0
      6851: fconst_0
      6852: invokestatic  #91                 // Method net/minecraft/class_7187.method_41823:(FFF)Lorg/joml/Vector3f;
      6855: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6858: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6861: aastore
      6862: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      6865: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      6868: ldc           #99                 // String Neck3
      6870: new           #14                 // class net/minecraft/class_7179
      6873: dup
      6874: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      6877: iconst_3
      6878: anewarray     #55                 // class net/minecraft/class_7186
      6881: dup
      6882: iconst_0
      6883: new           #55                 // class net/minecraft/class_7186
      6886: dup
      6887: fconst_0
      6888: fconst_0
      6889: fconst_0
      6890: fconst_0
      6891: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      6894: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6897: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6900: aastore
      6901: dup
      6902: iconst_1
      6903: new           #55                 // class net/minecraft/class_7186
      6906: dup
      6907: ldc           #230                // float 0.0833f
      6909: fconst_0
      6910: fconst_0
      6911: fconst_0
      6912: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      6915: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6918: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6921: aastore
      6922: dup
      6923: iconst_2
      6924: new           #55                 // class net/minecraft/class_7186
      6927: dup
      6928: fconst_1
      6929: fconst_0
      6930: fconst_0
      6931: fconst_0
      6932: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      6935: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6938: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6941: aastore
      6942: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      6945: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      6948: ldc           #107                // String Body6
      6950: new           #14                 // class net/minecraft/class_7179
      6953: dup
      6954: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      6957: iconst_3
      6958: anewarray     #55                 // class net/minecraft/class_7186
      6961: dup
      6962: iconst_0
      6963: new           #55                 // class net/minecraft/class_7186
      6966: dup
      6967: fconst_0
      6968: fconst_0
      6969: fconst_0
      6970: fconst_0
      6971: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      6974: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6977: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      6980: aastore
      6981: dup
      6982: iconst_1
      6983: new           #55                 // class net/minecraft/class_7186
      6986: dup
      6987: ldc           #230                // float 0.0833f
      6989: fconst_0
      6990: fconst_0
      6991: fconst_0
      6992: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      6995: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      6998: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7001: aastore
      7002: dup
      7003: iconst_2
      7004: new           #55                 // class net/minecraft/class_7186
      7007: dup
      7008: fconst_1
      7009: fconst_0
      7010: fconst_0
      7011: fconst_0
      7012: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      7015: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      7018: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7021: aastore
      7022: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      7025: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      7028: ldc           #113                // String Body7
      7030: new           #14                 // class net/minecraft/class_7179
      7033: dup
      7034: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      7037: iconst_3
      7038: anewarray     #55                 // class net/minecraft/class_7186
      7041: dup
      7042: iconst_0
      7043: new           #55                 // class net/minecraft/class_7186
      7046: dup
      7047: fconst_0
      7048: fconst_0
      7049: fconst_0
      7050: fconst_0
      7051: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      7054: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      7057: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7060: aastore
      7061: dup
      7062: iconst_1
      7063: new           #55                 // class net/minecraft/class_7186
      7066: dup
      7067: ldc           #230                // float 0.0833f
      7069: fconst_0
      7070: fconst_0
      7071: fconst_0
      7072: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      7075: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      7078: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7081: aastore
      7082: dup
      7083: iconst_2
      7084: new           #55                 // class net/minecraft/class_7186
      7087: dup
      7088: fconst_1
      7089: fconst_0
      7090: fconst_0
      7091: fconst_0
      7092: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      7095: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      7098: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7101: aastore
      7102: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      7105: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      7108: ldc           #119                // String Body8
      7110: new           #14                 // class net/minecraft/class_7179
      7113: dup
      7114: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      7117: iconst_3
      7118: anewarray     #55                 // class net/minecraft/class_7186
      7121: dup
      7122: iconst_0
      7123: new           #55                 // class net/minecraft/class_7186
      7126: dup
      7127: fconst_0
      7128: fconst_0
      7129: ldc           #213                // float -15.0f
      7131: fconst_0
      7132: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      7135: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      7138: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7141: aastore
      7142: dup
      7143: iconst_1
      7144: new           #55                 // class net/minecraft/class_7186
      7147: dup
      7148: ldc           #230                // float 0.0833f
      7150: fconst_0
      7151: ldc           #213                // float -15.0f
      7153: fconst_0
      7154: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      7157: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      7160: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7163: aastore
      7164: dup
      7165: iconst_2
      7166: new           #55                 // class net/minecraft/class_7186
      7169: dup
      7170: fconst_1
      7171: fconst_0
      7172: ldc           #213                // float -15.0f
      7174: fconst_0
      7175: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      7178: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      7181: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7184: aastore
      7185: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      7188: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      7191: ldc           #125                // String Body9
      7193: new           #14                 // class net/minecraft/class_7179
      7196: dup
      7197: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      7200: iconst_3
      7201: anewarray     #55                 // class net/minecraft/class_7186
      7204: dup
      7205: iconst_0
      7206: new           #55                 // class net/minecraft/class_7186
      7209: dup
      7210: fconst_0
      7211: fconst_0
      7212: ldc           #214                // float 72.5f
      7214: fconst_0
      7215: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      7218: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      7221: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7224: aastore
      7225: dup
      7226: iconst_1
      7227: new           #55                 // class net/minecraft/class_7186
      7230: dup
      7231: ldc           #230                // float 0.0833f
      7233: fconst_0
      7234: ldc           #214                // float 72.5f
      7236: fconst_0
      7237: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      7240: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      7243: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7246: aastore
      7247: dup
      7248: iconst_2
      7249: new           #55                 // class net/minecraft/class_7186
      7252: dup
      7253: fconst_1
      7254: fconst_0
      7255: ldc           #214                // float 72.5f
      7257: fconst_0
      7258: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      7261: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      7264: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7267: aastore
      7268: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      7271: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      7274: ldc           #131                // String Body10
      7276: new           #14                 // class net/minecraft/class_7179
      7279: dup
      7280: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      7283: iconst_3
      7284: anewarray     #55                 // class net/minecraft/class_7186
      7287: dup
      7288: iconst_0
      7289: new           #55                 // class net/minecraft/class_7186
      7292: dup
      7293: fconst_0
      7294: fconst_0
      7295: ldc           #139                // float 50.0f
      7297: fconst_0
      7298: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      7301: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      7304: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7307: aastore
      7308: dup
      7309: iconst_1
      7310: new           #55                 // class net/minecraft/class_7186
      7313: dup
      7314: ldc           #230                // float 0.0833f
      7316: fconst_0
      7317: ldc           #139                // float 50.0f
      7319: fconst_0
      7320: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      7323: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      7326: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7329: aastore
      7330: dup
      7331: iconst_2
      7332: new           #55                 // class net/minecraft/class_7186
      7335: dup
      7336: fconst_1
      7337: fconst_0
      7338: ldc           #139                // float 50.0f
      7340: fconst_0
      7341: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      7344: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      7347: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7350: aastore
      7351: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      7354: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      7357: ldc           #133                // String Tail4
      7359: new           #14                 // class net/minecraft/class_7179
      7362: dup
      7363: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      7366: iconst_3
      7367: anewarray     #55                 // class net/minecraft/class_7186
      7370: dup
      7371: iconst_0
      7372: new           #55                 // class net/minecraft/class_7186
      7375: dup
      7376: fconst_0
      7377: fconst_0
      7378: ldc           #129                // float 42.5f
      7380: fconst_0
      7381: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      7384: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      7387: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7390: aastore
      7391: dup
      7392: iconst_1
      7393: new           #55                 // class net/minecraft/class_7186
      7396: dup
      7397: ldc           #230                // float 0.0833f
      7399: fconst_0
      7400: ldc           #129                // float 42.5f
      7402: fconst_0
      7403: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      7406: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      7409: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7412: aastore
      7413: dup
      7414: iconst_2
      7415: new           #55                 // class net/minecraft/class_7186
      7418: dup
      7419: fconst_1
      7420: fconst_0
      7421: ldc           #129                // float 42.5f
      7423: fconst_0
      7424: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      7427: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      7430: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7433: aastore
      7434: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      7437: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      7440: ldc           #137                // String Tail5
      7442: new           #14                 // class net/minecraft/class_7179
      7445: dup
      7446: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      7449: iconst_3
      7450: anewarray     #55                 // class net/minecraft/class_7186
      7453: dup
      7454: iconst_0
      7455: new           #55                 // class net/minecraft/class_7186
      7458: dup
      7459: fconst_0
      7460: fconst_0
      7461: ldc           #97                 // float 47.5f
      7463: fconst_0
      7464: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      7467: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      7470: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7473: aastore
      7474: dup
      7475: iconst_1
      7476: new           #55                 // class net/minecraft/class_7186
      7479: dup
      7480: ldc           #230                // float 0.0833f
      7482: fconst_0
      7483: ldc           #97                 // float 47.5f
      7485: fconst_0
      7486: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      7489: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      7492: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7495: aastore
      7496: dup
      7497: iconst_2
      7498: new           #55                 // class net/minecraft/class_7186
      7501: dup
      7502: fconst_1
      7503: fconst_0
      7504: ldc           #97                 // float 47.5f
      7506: fconst_0
      7507: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      7510: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      7513: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7516: aastore
      7517: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      7520: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      7523: ldc           #142                // String Tail6
      7525: new           #14                 // class net/minecraft/class_7179
      7528: dup
      7529: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      7532: iconst_3
      7533: anewarray     #55                 // class net/minecraft/class_7186
      7536: dup
      7537: iconst_0
      7538: new           #55                 // class net/minecraft/class_7186
      7541: dup
      7542: fconst_0
      7543: ldc           #215                // float 35.9802f
      7545: ldc           #216                // float 6.1147f
      7547: ldc           #217                // float -16.0205f
      7549: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      7552: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      7555: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7558: aastore
      7559: dup
      7560: iconst_1
      7561: new           #55                 // class net/minecraft/class_7186
      7564: dup
      7565: ldc           #230                // float 0.0833f
      7567: ldc           #215                // float 35.9802f
      7569: ldc           #216                // float 6.1147f
      7571: ldc           #217                // float -16.0205f
      7573: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      7576: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      7579: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7582: aastore
      7583: dup
      7584: iconst_2
      7585: new           #55                 // class net/minecraft/class_7186
      7588: dup
      7589: fconst_1
      7590: ldc           #215                // float 35.9802f
      7592: ldc           #216                // float 6.1147f
      7594: ldc           #217                // float -16.0205f
      7596: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      7599: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      7602: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7605: aastore
      7606: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      7609: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      7612: ldc           #221                // String Face22
      7614: new           #14                 // class net/minecraft/class_7179
      7617: dup
      7618: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      7621: iconst_3
      7622: anewarray     #55                 // class net/minecraft/class_7186
      7625: dup
      7626: iconst_0
      7627: new           #55                 // class net/minecraft/class_7186
      7630: dup
      7631: fconst_0
      7632: fconst_0
      7633: fconst_0
      7634: fconst_0
      7635: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      7638: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      7641: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7644: aastore
      7645: dup
      7646: iconst_1
      7647: new           #55                 // class net/minecraft/class_7186
      7650: dup
      7651: ldc           #230                // float 0.0833f
      7653: ldc           #129                // float 42.5f
      7655: fconst_0
      7656: fconst_0
      7657: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      7660: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      7663: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7666: aastore
      7667: dup
      7668: iconst_2
      7669: new           #55                 // class net/minecraft/class_7186
      7672: dup
      7673: fconst_1
      7674: ldc           #129                // float 42.5f
      7676: fconst_0
      7677: fconst_0
      7678: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      7681: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      7684: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7687: aastore
      7688: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      7691: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      7694: ldc           #219                // String Face3
      7696: new           #14                 // class net/minecraft/class_7179
      7699: dup
      7700: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      7703: iconst_3
      7704: anewarray     #55                 // class net/minecraft/class_7186
      7707: dup
      7708: iconst_0
      7709: new           #55                 // class net/minecraft/class_7186
      7712: dup
      7713: fconst_0
      7714: fconst_0
      7715: fconst_0
      7716: fconst_0
      7717: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      7720: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      7723: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7726: aastore
      7727: dup
      7728: iconst_1
      7729: new           #55                 // class net/minecraft/class_7186
      7732: dup
      7733: ldc           #230                // float 0.0833f
      7735: ldc           #100                // float -30.0f
      7737: fconst_0
      7738: fconst_0
      7739: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      7742: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      7745: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7748: aastore
      7749: dup
      7750: iconst_2
      7751: new           #55                 // class net/minecraft/class_7186
      7754: dup
      7755: fconst_1
      7756: ldc           #100                // float -30.0f
      7758: fconst_0
      7759: fconst_0
      7760: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      7763: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      7766: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7769: aastore
      7770: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      7773: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      7776: ldc           #223                // String Tongue
      7778: new           #14                 // class net/minecraft/class_7179
      7781: dup
      7782: getstatic     #53                 // Field net/minecraft/class_7179$class_7183.field_37887:Lnet/minecraft/class_7179$class_7182;
      7785: iconst_3
      7786: anewarray     #55                 // class net/minecraft/class_7186
      7789: dup
      7790: iconst_0
      7791: new           #55                 // class net/minecraft/class_7186
      7794: dup
      7795: fconst_0
      7796: fconst_0
      7797: fconst_0
      7798: fconst_0
      7799: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      7802: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      7805: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7808: aastore
      7809: dup
      7810: iconst_1
      7811: new           #55                 // class net/minecraft/class_7186
      7814: dup
      7815: ldc           #230                // float 0.0833f
      7817: ldc           #126                // float 22.5f
      7819: fconst_0
      7820: fconst_0
      7821: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      7824: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      7827: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7830: aastore
      7831: dup
      7832: iconst_2
      7833: new           #55                 // class net/minecraft/class_7186
      7836: dup
      7837: fconst_1
      7838: ldc           #126                // float 22.5f
      7840: fconst_0
      7841: fconst_0
      7842: invokestatic  #62                 // Method net/minecraft/class_7187.method_41829:(FFF)Lorg/joml/Vector3f;
      7845: getstatic     #85                 // Field net/minecraft/class_7179$class_7181.field_37884:Lnet/minecraft/class_7179$class_7180;
      7848: invokespecial #69                 // Method net/minecraft/class_7186."<init>":(FLorg/joml/Vector3fc;Lnet/minecraft/class_7179$class_7180;)V
      7851: aastore
      7852: invokespecial #75                 // Method net/minecraft/class_7179."<init>":(Lnet/minecraft/class_7179$class_7182;[Lnet/minecraft/class_7186;)V
      7855: invokevirtual #79                 // Method net/minecraft/class_7184$class_7185.method_41820:(Ljava/lang/String;Lnet/minecraft/class_7179;)Lnet/minecraft/class_7184$class_7185;
      7858: invokevirtual #156                // Method net/minecraft/class_7184$class_7185.method_41821:()Lnet/minecraft/class_7184;
      7861: putstatic     #235                // Field ANIM_RATTLESNAKE_ATTACK:Lnet/minecraft/class_7184;
      7864: return
      LineNumberTable:
        line 9: 0
        line 11: 32
        line 12: 54
        line 13: 76
        line 10: 89
        line 16: 116
        line 15: 129
        line 19: 155
        line 18: 168
        line 22: 195
        line 21: 208
        line 25: 235
        line 24: 248
        line 28: 275
        line 29: 297
        line 30: 319
        line 31: 341
        line 32: 363
        line 27: 376
        line 35: 403
        line 36: 425
        line 37: 447
        line 38: 469
        line 39: 491
        line 34: 504
        line 42: 531
        line 43: 553
        line 44: 575
        line 45: 597
        line 46: 619
        line 41: 632
        line 49: 659
        line 50: 681
        line 51: 703
        line 52: 725
        line 53: 747
        line 48: 760
        line 56: 787
        line 57: 809
        line 58: 831
        line 59: 853
        line 60: 875
        line 55: 888
        line 63: 915
        line 64: 937
        line 65: 959
        line 66: 981
        line 67: 1003
        line 62: 1016
        line 70: 1043
        line 71: 1065
        line 72: 1087
        line 73: 1109
        line 74: 1131
        line 69: 1144
        line 77: 1171
        line 78: 1193
        line 79: 1215
        line 80: 1237
        line 81: 1259
        line 76: 1272
        line 84: 1300
        line 85: 1323
        line 86: 1346
        line 87: 1369
        line 88: 1392
        line 83: 1405
        line 90: 1408
        line 92: 1414
        line 94: 1445
        line 95: 1467
        line 96: 1488
        line 93: 1501
        line 99: 1528
        line 98: 1541
        line 102: 1567
        line 101: 1580
        line 105: 1607
        line 106: 1629
        line 107: 1651
        line 108: 1673
        line 109: 1694
        line 104: 1707
        line 112: 1734
        line 111: 1747
        line 115: 1774
        line 114: 1787
        line 118: 1814
        line 119: 1836
        line 120: 1858
        line 121: 1880
        line 122: 1901
        line 117: 1914
        line 125: 1941
        line 126: 1963
        line 127: 1985
        line 128: 2007
        line 129: 2028
        line 124: 2041
        line 132: 2068
        line 133: 2090
        line 134: 2112
        line 135: 2134
        line 136: 2155
        line 131: 2168
        line 139: 2195
        line 140: 2217
        line 141: 2239
        line 142: 2261
        line 143: 2282
        line 138: 2295
        line 146: 2322
        line 147: 2344
        line 148: 2366
        line 149: 2388
        line 150: 2409
        line 145: 2422
        line 153: 2449
        line 154: 2471
        line 155: 2493
        line 156: 2515
        line 157: 2536
        line 152: 2549
        line 160: 2576
        line 161: 2598
        line 162: 2620
        line 163: 2642
        line 164: 2663
        line 159: 2676
        line 167: 2703
        line 168: 2725
        line 169: 2747
        line 170: 2769
        line 171: 2790
        line 166: 2803
        line 174: 2830
        line 173: 2843
        line 176: 2846
        line 178: 2852
        line 180: 2884
        line 179: 2897
        line 183: 2924
        line 182: 2937
        line 186: 2964
        line 185: 2977
        line 189: 3004
        line 188: 3017
        line 192: 3044
        line 191: 3057
        line 195: 3084
        line 194: 3097
        line 198: 3124
        line 197: 3137
        line 201: 3164
        line 200: 3177
        line 204: 3204
        line 203: 3217
        line 207: 3244
        line 206: 3257
        line 210: 3284
        line 209: 3297
        line 213: 3324
        line 212: 3337
        line 216: 3366
        line 215: 3379
        line 218: 3382
        line 220: 3388
        line 222: 3420
        line 221: 3433
        line 225: 3462
        line 224: 3475
        line 228: 3503
        line 227: 3516
        line 231: 3545
        line 230: 3558
        line 234: 3587
        line 233: 3600
        line 237: 3629
        line 236: 3642
        line 240: 3671
        line 239: 3684
        line 243: 3713
        line 242: 3726
        line 246: 3755
        line 245: 3768
        line 249: 3795
        line 248: 3808
        line 252: 3834
        line 251: 3847
        line 255: 3873
        line 254: 3886
        line 258: 3912
        line 257: 3925
        line 261: 3954
        line 260: 3967
        line 264: 3993
        line 263: 4006
        line 267: 4032
        line 266: 4045
        line 270: 4071
        line 269: 4084
        line 273: 4110
        line 272: 4123
        line 276: 4150
        line 275: 4163
        line 279: 4190
        line 278: 4203
        line 282: 4230
        line 281: 4243
        line 285: 4270
        line 284: 4283
        line 288: 4310
        line 287: 4323
        line 291: 4352
        line 290: 4365
        line 294: 4391
        line 293: 4404
        line 297: 4430
        line 296: 4443
        line 300: 4469
        line 299: 4482
        line 302: 4485
        line 304: 4491
        line 306: 4520
        line 305: 4533
        line 309: 4560
        line 308: 4573
        line 312: 4602
        line 311: 4615
        line 315: 4643
        line 314: 4656
        line 318: 4685
        line 317: 4698
        line 321: 4727
        line 320: 4740
        line 324: 4769
        line 323: 4782
        line 327: 4811
        line 326: 4824
        line 330: 4853
        line 329: 4866
        line 333: 4895
        line 332: 4908
        line 336: 4935
        line 335: 4948
        line 339: 4974
        line 338: 4987
        line 342: 5013
        line 341: 5026
        line 345: 5052
        line 344: 5065
        line 348: 5094
        line 347: 5107
        line 351: 5133
        line 350: 5146
        line 354: 5172
        line 353: 5185
        line 357: 5211
        line 356: 5224
        line 360: 5250
        line 359: 5263
        line 363: 5290
        line 362: 5303
        line 366: 5330
        line 365: 5343
        line 369: 5370
        line 368: 5383
        line 372: 5410
        line 371: 5423
        line 375: 5450
        line 374: 5463
        line 378: 5492
        line 377: 5505
        line 381: 5531
        line 380: 5544
        line 384: 5570
        line 383: 5583
        line 386: 5586
        line 388: 5592
        line 390: 5620
        line 391: 5641
        line 392: 5661
        line 389: 5674
        line 395: 5703
        line 396: 5727
        line 397: 5750
        line 394: 5763
        line 400: 5791
        line 401: 5812
        line 402: 5832
        line 399: 5845
        line 405: 5874
        line 406: 5898
        line 407: 5921
        line 404: 5934
        line 410: 5963
        line 411: 5985
        line 412: 6006
        line 409: 6019
        line 415: 6048
        line 416: 6072
        line 417: 6095
        line 414: 6108
        line 420: 6137
        line 421: 6159
        line 422: 6180
        line 419: 6193
        line 425: 6222
        line 426: 6246
        line 427: 6269
        line 424: 6282
        line 430: 6311
        line 431: 6333
        line 432: 6354
        line 429: 6367
        line 435: 6394
        line 436: 6416
        line 437: 6437
        line 434: 6450
        line 440: 6476
        line 441: 6499
        line 442: 6521
        line 439: 6534
        line 445: 6560
        line 446: 6582
        line 447: 6603
        line 444: 6616
        line 450: 6642
        line 451: 6663
        line 452: 6683
        line 449: 6696
        line 455: 6725
        line 456: 6749
        line 457: 6772
        line 454: 6785
        line 460: 6811
        line 461: 6832
        line 462: 6852
        line 459: 6865
        line 465: 6891
        line 466: 6912
        line 467: 6932
        line 464: 6945
        line 470: 6971
        line 471: 6992
        line 472: 7012
        line 469: 7025
        line 475: 7051
        line 476: 7072
        line 477: 7092
        line 474: 7105
        line 480: 7132
        line 481: 7154
        line 482: 7175
        line 479: 7188
        line 485: 7215
        line 486: 7237
        line 487: 7258
        line 484: 7271
        line 490: 7298
        line 491: 7320
        line 492: 7341
        line 489: 7354
        line 495: 7381
        line 496: 7403
        line 497: 7424
        line 494: 7437
        line 500: 7464
        line 501: 7486
        line 502: 7507
        line 499: 7520
        line 505: 7549
        line 506: 7573
        line 507: 7596
        line 504: 7609
        line 510: 7635
        line 511: 7657
        line 512: 7678
        line 509: 7691
        line 515: 7717
        line 516: 7739
        line 517: 7760
        line 514: 7773
        line 520: 7799
        line 521: 7821
        line 522: 7842
        line 519: 7855
        line 524: 7858
        line 388: 7864
}
InnerClasses:
  public static #10= #7 of #9;            // class_7185=class net/minecraft/class_7184$class_7185 of class net/minecraft/class_7184
  public static #15= #12 of #14;          // class_7183=class net/minecraft/class_7179$class_7183 of class net/minecraft/class_7179
  public static #18= #17 of #14;          // class_7182=class net/minecraft/class_7179$class_7182 of class net/minecraft/class_7179
  public static #21= #20 of #14;          // class_7181=class net/minecraft/class_7179$class_7181 of class net/minecraft/class_7179
  public static #24= #23 of #14;          // class_7180=class net/minecraft/class_7179$class_7180 of class net/minecraft/class_7179
SourceFile: "RattleSnakeAnimations.java"

## Skeleton/passenger string matches
