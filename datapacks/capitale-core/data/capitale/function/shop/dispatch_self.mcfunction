# 1.5.5 hotfix commerce EasyNPC — conversations only.
# Les achats par trigger ShopChoix restent désactivés : les achats réels passent par l’action EasyNPC Open Trading Menu.
# Les triggers 80/180/280 sont seulement des fallbacks chat si le bouton EasyNPC n’est pas encore câblé.

# Comptoir des quais — anciens achats directs désactivés
execute if score @s ShopChoix matches 1 run function capitale:shop/easynpc_purchase_disabled_self
execute if score @s ShopChoix matches 2 run function capitale:shop/easynpc_purchase_disabled_self
execute if score @s ShopChoix matches 3 run function capitale:shop/easynpc_purchase_disabled_self
execute if score @s ShopChoix matches 4 run function capitale:shop/easynpc_purchase_disabled_self
execute if score @s ShopChoix matches 5 run function capitale:shop/easynpc_purchase_disabled_self
execute if score @s ShopChoix matches 80 run function capitale:shop/quais_boissons/open_shop_hint_self
execute if score @s ShopChoix matches 81 run function capitale:shop/quais_boissons/leave_self
execute if score @s ShopChoix matches 82 run function capitale:shop/quais_boissons/talk_self

# Librairie agréée — anciens achats directs désactivés
execute if score @s ShopChoix matches 101 run function capitale:shop/easynpc_purchase_disabled_self
execute if score @s ShopChoix matches 102 run function capitale:shop/easynpc_purchase_disabled_self
execute if score @s ShopChoix matches 103 run function capitale:shop/easynpc_purchase_disabled_self
execute if score @s ShopChoix matches 104 run function capitale:shop/easynpc_purchase_disabled_self
execute if score @s ShopChoix matches 105 run function capitale:shop/easynpc_purchase_disabled_self
execute if score @s ShopChoix matches 106 run function capitale:shop/easynpc_purchase_disabled_self
execute if score @s ShopChoix matches 180 run function capitale:npc/libraire/open_shop_hint_self
execute if score @s ShopChoix matches 181 run function capitale:npc/libraire/interact_self

# Armurier — anciens achats directs désactivés
execute if score @s ShopChoix matches 201 run function capitale:shop/easynpc_purchase_disabled_self
execute if score @s ShopChoix matches 202 run function capitale:shop/easynpc_purchase_disabled_self
execute if score @s ShopChoix matches 203 run function capitale:shop/easynpc_purchase_disabled_self
execute if score @s ShopChoix matches 204 run function capitale:shop/easynpc_purchase_disabled_self
execute if score @s ShopChoix matches 205 run function capitale:shop/easynpc_purchase_disabled_self
execute if score @s ShopChoix matches 206 run function capitale:shop/easynpc_purchase_disabled_self
execute if score @s ShopChoix matches 207 run function capitale:shop/easynpc_purchase_disabled_self
execute if score @s ShopChoix matches 208 run function capitale:shop/easynpc_purchase_disabled_self
execute if score @s ShopChoix matches 280 run function capitale:npc/armurier_port/open_shop_hint_self
execute if score @s ShopChoix matches 281 run function capitale:npc/armurier_port/talk_self
execute if score @s ShopChoix matches 282 run function capitale:npc/armurier_port/leave_self
