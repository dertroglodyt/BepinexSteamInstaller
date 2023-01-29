# BepinexSteamInstaller

Setting up BepInEx in a steam game folder can be quite challenging for a less experienced user.
This is where the installer steps in.

It's a small util to automate the installation of BepInEx into the correct directory of a steam game.
It downloads BepInEx from Github and unzips it to a folder inside the game directory. 
Two symbolic links are created to make current or future steam workshop mods that depend on BepInEx work out of the box.

**For the two symbolic links to be created the tool has to be run with admin rights.**

Start by running **_installer.bat_** from a command line or right clicking in file explorer.

Can be easily configured and extended to other games by editing **_config.csv_**:
- First line specifies the steam installation directory.
- Second line specifies the github repository and desired version of BepInEx to install.
- Any following line holds a game name and the corresponding workshop ID.

The workshop ID of a game can be found by going to your abos for the game in your steam app and right clicking the workshop tab.
Copy the page link and paste it into an editor. The six digit number embedded in the link is the workshop ID.

As this is a java program you need to have java installed on your PC.