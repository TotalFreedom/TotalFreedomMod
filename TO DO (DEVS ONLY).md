#
#Stuff to add, mark when done.
#Meta in Essentials Format.
#
#{PLAYER} = Specified Player
#{USER} = Player Running Command (CONSOLE if ran buý Console

Banana Command:
  Usage:/banana
    execute:GIVE_ITEM_ALL
      item:GOLDEN_CARROT
      name:&e&lBanana
    execute:DISPLAY_TEXT
      text:&6Banana is Love, Banana is Life|Give it your Hope, and I will be nice.

Developer Rank:
  Prefix:&8[&dDeveloper&8]
  Permissions: Same as SrA
  Players:LucazDaKing
  Unsure about this as you said that I took Lucaz's spot

Lead-Developer Rank:
  Prefix:&8[&dLead-Developer&8]
  Permissions: Same as SrA
  Players:ImALuckyGuy
Done by PacksGamingHD


Co-Owner Rank:
  Prefix:&8[&9Co-Owner&8]
  Permissions:Same as SrA
  Players: SilentSilence, _DiamondFox_
  Done by PacksGamingHD

Custom Join Sound:
  Usage:CONFIG_FILE
    execute: Plays {SOUND} When {PLAYER} joins.

BananaHammer:
  Usage:/bananahammer {PLAYER}
    execute:DISPLAY_TEXT
      text:&e&l{PLAYER} got Rekt by {USER} Using the Legendary Banana Hammer!
      text:&e&l{PLAYER} got Rekt by {USER} Using the Legendary Banana Hammer!
      text:&e&l{PLAYER} got Rekt by {USER} Using the Legendary Banana Hammer!
      text:&e&l{PLAYER} got Rekt by {USER} Using the Legendary Banana Hammer!
      text:&e&l{PLAYER} got Rekt by {USER} Using the Legendary Banana Hammer!
    execute:BAN_PLAYER
      player:{PLAYER}
      duration:PERMANENT
      ban_message:&e&lThe Banana Hammer Has Spoken!
