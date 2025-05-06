# NIN II Handin README
#### Fill out this README before turning in this project. Make sure to fill this out again for each assignment!

### Banner ID: B01998854

### Already uploaded demo onto Slack:

### Git Repository Link: 

## Primary Engine Requirements:
| Requirement                                                                             | Location in code or steps to view in game        |
|-----------------------------------------------------------------------------------------|--------------------------------------------------|
| Your handin must meet all global requirements.                                          | Yes |
| Your engine must correctly implement saving and loading through serialization.          | Yes, realted codes are shown in "/engine/systems/SaveAndLoadSystem.class," which supports the game engine with saving and loading through serialization based on XML files. |
| Your engine must correctly support raycasting for polygons and AABs.                    | Yes, realted codes are shown in "/engine/gameobjects/RayColliderComponent.class," where ray collision with polygons and AABs has been added. |
| You must complete the debugger to demonstrate correct raycasting for polygons and AABs. | Yes, it can be seen after running the "Display.class." |

## Primary Game Requirements:
| Requirement                                                                                                                                                                                                     | Location in code or steps to view in game                             |
|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------|
| Your player must be able to fire projectiles.                                                                                                                                                                   | Yes. If there is an object the projectiles can hit, then after pressing 'space key', you can see a ray projectile. |
| Your game must be loaded from a file. For this requirement, you can save your game using any file type, formatted as you please. You must provide at least one file that we can load in your game successfully. | Yes, there are two xml files, which are 'save.xml' for a new game and 'save.xml' for a saved game. You can start (restart) or continue a game through related buttons shown on 'title screen' and 'menu screen.' |
| You must be able to save your game state, restart the game, and then load that game state.                                                                                                                      | Yes, when you open a new game or continue a game, a 'menu button' can be seen in the left upper corner of the "in game screen," where you can "save," "restart" and "load" your game. |
| The player must always be in view.                                                                                                                                                                              | Yes, a center view component from previous projects are used here too! |
| It must be possible to start a new game without restarting the program.                                                                                                                                         | Yes, as said above, through the "menu screen" you can restart a game without restarting the program. |

## Secondary Engine Requirements:
| Requirement                                                                   | Location in code or steps to view in game |
|-------------------------------------------------------------------------------|-------------------------------------------|
| Your engine must meet all primary engine requirements.                        | Yes |
| Your engine must correctly support raycasting for circles.                    | Yes, realted codes are shown in "/engine/gameobjects/RayColliderComponent.class," where ray collision with circles has been added. | 
| You must complete the debugger to demonstrate correct raycasting for circles. | Yes, also shown in the "Display.class." |

## Secondary Game Requirements:
| Requirement                                                                                                                                                              | Location in code or steps to view in game |
|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------|
| Your game must meet all primary game requirements.                                                                                                                       | Yes |
| There must be a polished UI for saving and loading.                                                                                                                      | Yes, after opening a game, you press the "menu button" shown in the left upper corner, then you can open a menu screen that contains "saving" and "loading." |
| Save files must be written in XML format. This will help organize your saves, and also java has code for parsing these files.                                            | Yes, the saved files (XML) are in the "/Nin/save.xml and start.xml." |
| The player must be able to fire projectiles that travel instantly using raycasting. Projectiles must apply an impulse to whatever they hit in the direction of that ray. | Yes, the is a monster goblin on your left when you open the game, try shooting it with sapce key, and you will see the goblin will be moving because of the impulse caused by the ray. |
| Your game must meet at least two of the extra game requirements.                                                                                                         | Yes, it will be explained in the below part. |

## Extra Game Requirements:
| Requirement                                                                                                                                                                                                 | Location in code or steps to view in game                                                                              |
|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------|
| Make a destructible environment, such as breakable blocks.                                                                                                                                                                                           | Yes, when you open the game, you will see sand-like blocks on your left. Try shooting them with projectile rays. Or you can just move to them and they will also disapear when you collide with them. |
| The player and enemies are drawn with sprites (and animations when appropriate) instead of vector graphics.                                                                                                                                                                                           | Yes, as shown in the game. |
| A non-trivial puzzle that must be solved in order to win the game or complete a level.                                                                                                                                                                                            | Yes, there will be an end tile that you need to find to win the game. Try to break the sand blocks on the road. And jump a long distance to find the end tile that help you win the game. |
| A polished graphical UI system for non-game elements such as menus, with at minimum an options screen with at least two gameplay/control options                                                                                                                                                                                           | Yes, a menu screen is added to this game. You can open this screen when you press the "menu button" shown in the left upper corner when you enter the game. There are many different options apart from "save" and "load." |


[//]: # (| Requirement | Location in code or steps to view in game  |)

[//]: # (|---|---|)

[//]: # (| [Copy and paste from handout] | ```File path, function name, or steps to replicate``` |)

--------------------------------------------------------------

Known bugs:
1. After making both moving and attacking, the player-controller cannot change back to the previous animation after finishing the current animation.

Hours spent on assignment: 25hrs
