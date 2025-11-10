package ninco.business.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * <b>Record</b> interface representing an entity with a creation timestamp.
 * It provides a method to retrieve the creation date and a default method to format it.
 */
public interface Record {
  /**
   * Retrieves the creation date of the record.
   *
   * @return Creation date as LocalDateTime.
   */
  LocalDateTime getCreatedAt();

  /**
   * Formats the creation date into a human-readable string.
   *
   * @return Formatted creation date string.
   */
  default String getFormattedCreatedAt() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm", Locale.forLanguageTag("en-US"));
    return getCreatedAt().format(formatter);
  }
}