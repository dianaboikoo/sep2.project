package ViewModel;

public class FieldError
{
  private String fieldName;
  private String message;

  public FieldError(String fieldName, String message)
  {
    this.fieldName = fieldName;
    this.message = message;
  }

  public String getFieldName() { return fieldName; }
  public String getMessage() { return message; }

  @Override
  public String toString()
  {
    return fieldName + ": " + message;
  }
}