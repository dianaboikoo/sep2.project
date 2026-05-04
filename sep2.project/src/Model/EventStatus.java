package Model;

public enum EventStatus
{
  DRAFT,
  PUBLISHED,
  CANCELLED;

  public boolean isActive()
  {
    return this == PUBLISHED;
  }
}