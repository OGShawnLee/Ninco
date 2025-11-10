package ninco.gui.controller;

/**
 * ContextController is an interface for controllers that require context data.
 * It defines a method to set the context for the controller.
 *
 * @param <T> the type of the context data
 */
public interface ContextController<T> {
  void setContext(T data);
}