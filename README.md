# Serverlist Buffer Crash Fixer

This fork fixes 2 crashes than original mod:

`java.lang.ClassNotFoundException: net.minecraft.client.gui.ServerListEntryNormal$Anonymous$RANDOM_NUMBER`

`java.lang.NullPointerException: Ticking screen 
at io.netty.bootstrap.Bootstrap.checkAddress(Bootstrap.java:255)`

# Original Desc:
A mod that fixes the slow/infinite server data loading in the multiplayer menu. 

Also prevents you from spamming "refresh" too much.

### Note that this "behavior" *(read bug)* is still present as of the latest versions (1.20.1 currently).


## [Alternative for newer inferior versions (1.20)](https://modrinth.com/mod/serverpingerfixer)

# Technical explanation of what this mod does
This mod does 3 things to fix up that mess of a server pinger, all of which are in the `ServerListEntryNormal`

## Count the number of running ping tasks
& prevent you from starting too much. This greatly improves things especially when you spam "refresh", as you're now capped to serverCount * 2 tasks, unlike in vanilla where you can basically queue infinitely many tasks.  

When too many tasks are already running and you try to refresh, the server text will be set to "Spamming...", in which case you'll have to wait a few sec and hit refresh again

## Change the number of the server pinger's max concurrent task count
(the pinger is `field_148302_b` in the used 1.8.9 mappings, which is a `ScheduledThreadPoolExecutor`)

By default, this is set to 5, which is REALLY low, especially if you have a lot of servers.

Even more so if you had some servers that failed to ping, in which case you'd usually get stuck completely after only a few refreshes at most.

This is now changed to `serverCount + 5`, which is eg if you have 21 servers is 26.
*(Note that this gets caped to "Max threadcount (Pinger)", which is by default 50. So if you have 46 or 80 or 999 servers in your serverlist, you'll still only have at most 50 concurrent tasks if using the default config)*

## Use a proper timeout
This is made by adding another `ScheduledThreadPoolExecutor`, and then adding ping tasks to it using `final Future<?> future = timeoutExecutor.submit(getPingTask());`

The `field_148302_b.submit(...)` (pinger) vanilla calls are then replaced with ones that call `future.get(timeout)` instead of the ping task directly, causing the pinger to not get overloaded and to properly fail on timeouts after by default 4 seconds (which you can change in the config).

# Port to newer versions
I currently only have plans to port up to 1.12 (Forge obviously), if possible using ReplayMod's preprocessor to keep a single codebase. I didn't look at 1.12's code yet, but knowing mojang I doubt they changed the class I mixin into to a point it'd need a serious rewrite.

Once that's done, i'll (maybe) consider newer versions, and if I do i'll probably support as much versions as possible
