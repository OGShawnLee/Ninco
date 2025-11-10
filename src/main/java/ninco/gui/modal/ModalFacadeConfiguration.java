package ninco.gui.modal;

import java.util.Optional;

/**
 * ModalFacadeConfiguration is a configuration class for creating modals.
 * It holds the title, resource file name, and an optional onClose action.
 * It is used to configure the appearance and behavior of modals in the application.
 */
public class ModalFacadeConfiguration {
  private final String title;
  private final String resourceFileName;
  private Runnable onClose;

  public ModalFacadeConfiguration(String title, String resourceFileName) {
    this.title = title;
    this.resourceFileName = resourceFileName;
  }

  public ModalFacadeConfiguration(String title, String resourceFileName, Runnable onClose) {
    this.title = title;
    this.resourceFileName = resourceFileName;
    this.onClose = onClose;
  }

  public String getTitle() {
    return title;
  }

  public String getResourceFileName() {
    return resourceFileName;
  }

  public Optional<Runnable> getOnClose() {
    return Optional.ofNullable(onClose);
  }
}