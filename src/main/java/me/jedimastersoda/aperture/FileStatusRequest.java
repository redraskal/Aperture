package me.jedimastersoda.aperture;

import java.util.UUID;

import lombok.Getter;

public abstract class FileStatusRequest {

  @Getter private final String filePath;
  @Getter private final UUID uuid;

  public FileStatusRequest(String filePath) {
    this.filePath = filePath;
    this.uuid = UUID.randomUUID();
  }

  public abstract void complete(FileStatus fileStatus);
}