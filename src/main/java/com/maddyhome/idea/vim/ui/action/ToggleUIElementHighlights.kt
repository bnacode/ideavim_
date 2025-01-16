package com.maddyhome.idea.vim.ui.action
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.wm.WindowManager
import java.awt.AWTEvent
import java.awt.Color
import java.awt.Component
import java.awt.Container
import java.awt.KeyboardFocusManager
import java.awt.Toolkit
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import javax.swing.*

// Note: Dropdown menus are not currently highlighted or handled.

class ToggleUIElementHighlights : AnAction() {
  private var highlightMode = false
  private var originalColor = Color.BLACK
  private var rootComponent : Component? = null

  init {
    // Register global event listeners
    Toolkit.getDefaultToolkit().addAWTEventListener(
      { event ->
        val focusedComponent = KeyboardFocusManager.getCurrentKeyboardFocusManager().focusOwner
        println("Focused Component: $focusedComponent")

        // Check if highlight mode is active
        if (highlightMode) {

          // Check for mouse click
          if (event is MouseEvent && event.id == MouseEvent.MOUSE_CLICKED) {
            println("RESETOVANJE MOUSE Event: $event")
            rootComponent?.let {
              resetColor(it, originalColor)
            }
            return@addAWTEventListener
          }

          // Check for key press
          if (event is KeyEvent && event.id == KeyEvent.KEY_PRESSED) {

            // Handle key press reset for all other key events
            println("RESETOVANJE KEY Event: $event")
            rootComponent?.let {
              resetColor(it, originalColor)
            }
            return@addAWTEventListener
          }
        }
      },
      AWTEvent.MOUSE_EVENT_MASK or AWTEvent.KEY_EVENT_MASK
    )
  }

  override fun actionPerformed(e: AnActionEvent) {
    println("Triggered")
    // Safely get the root component, return if it's null
    rootComponent = e.project?.let {
      WindowManager.getInstance().getIdeFrame(it)?.component
    }

    // If rootComponent is not null, proceed to toggle highlights
    rootComponent?.let {
      if (originalColor == Color.BLACK) {
        originalColor = it.background // Store the original color if not set yet
      }
      println("SETOVANJE")
      if(!highlightMode)
        setColor(it, Color.PINK)
    }
  }

  private fun setColor(component: Component, color: Color) {
    highlightMode = true
    applyColor(component, color)
  }

  private fun applyColor(component: Component, color: Color){

    if (component is JComponent && isClickable(component) && component.isShowing() && component.componentCount == 0) {
      // Apply color to leaf component
      component.background = color
    }

    if (component is Container) {
      for (child in component.components) {
        applyColor(child, color)
      }
    }
  }

  private fun isClickable(component: Component): Boolean {

    if (component is AbstractButton || component is JMenuItem || component is JComboBox<*>) {
      return true
    }

    // Check for mouse listeners
    if (component.mouseListeners.isNotEmpty()) {
      return true
    }

    return false
  }

  private fun resetColor(component: Component, color: Color) {
    highlightMode = false
    applyColor(component, color)
  }

  private fun toggleHighlights(root: Component) {
    if (!highlightMode) {
      setColor(root, Color.PINK) // Use pink to highlight
    } else {
      resetColor(root, originalColor)  // Restore the original color
    }
  }

}