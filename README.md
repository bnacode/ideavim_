# IdeaVim Plugin Modifications

This repository contains modifications I made to the [IdeaVim plugin](https://github.com/JetBrains/ideavim), an IntelliJ IDEA plugin that emulates Vim's keybindings and behavior. These changes were implemented as part of my exploration into extending Vim-like functionality within JetBrains IDEs.

## Disclaimer
This is **not an official release** of the IdeaVim plugin. It is a personal project I worked on to explore plugin development and enhance my understanding of IntelliJ Platform SDK and Vim-like functionalities.

## Features Added

### 1. Reverse Line Action
- **Shortcut**: `\r` (Normal Mode)
- **Description**: Adds a custom Vim normal mode command to reverse the order of lines in a selection. This is inspired by Vim's extensibility and allows users to perform a quick reversal of text content.

### 2. Toggle Highlight on Clickable Elements
- **Behavior**: Toggles a highlight overlay on all clickable UI elements in the IDE, emulating Vim's behavior when you use commands like `Ctrl+Shift+H`. Clickable elements include buttons, menu items, and other interactable components.
- **Highlight Mode**: The mode is activated or deactivated via a custom shortcut, and highlights are removed upon any mouse click or keypress.

## Installation
To use this modified version, you can clone the repository and build the plugin locally. Note that this project is intended for educational purposes and may not meet production standards.

## Acknowledgments
Special thanks to the [IdeaVim](https://github.com/JetBrains/ideavim) maintainers and contributors for their work on the original plugin, which served as the foundation for these modifications.

## License
This project is subject to the same license as the original IdeaVim repository. Refer to the [LICENSE](https://github.com/JetBrains/ideavim/blob/master/LICENSE.txt) file for details.



