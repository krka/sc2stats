# What is this?

This is a SC2 ladder data collector (mmr, race, player)
and a visualizer.
It shows MMR distribution of players per race.

# Braindump of API information 
Using API's from: https://develop.battle.net/

## Extra API
https://us.api.battle.net/data/sc2/season/:ID

https://us.api.battle.net/data/sc2/season/current

https://us.api.battle.net/data/sc2/league/:SEASON_ID/:QUEUE_ID/:TEAM_TYPE/:LEAGUE_ID

### League Constants

The league endpoint has ID values that aren't particularly human friendly. We don't currently have an endpoint or fields for human readable representations of those IDs. In the mean time, here are some to get you started.

### Queue ID
 - 1 - Wings of Liberty 1v1
 - 2 - Wings of Liberty 2v2
 - 3 - Wings of Liberty 3v3
 - 4 - Wings of Liberty 4v4
 - 101 - Heart of the Swarm 1v1
 - 102 - Heart of the Swarm 2v2
 - 103 - Heart of the Swarm 3v3
 - 104 - Heart of the Swarm 4v4
 - 201 - Legacy of the Void 1v1
 - 202 - Legacy of the Void 2v2
 - 203 - Legacy of the Void 3v3
 - 204 - Legacy of the Void 4v4
 - 206 - Legacy of the Void Archon

### Team Type
 - 0 - Arranged
 - 1 - Random

### League ID
 - 0 - Bronze
 - 1 - Silver
 - 2 - Gold
 - 3 - Platinum
 - 4 - Diamond
 - 5 - Master
 - 6 - Grandmaster


https://us.api.battle.net/data/sc2/ladder/:ID

### Source
See: https://us.battle.net/forums/en/sc2/topic/20749724960

