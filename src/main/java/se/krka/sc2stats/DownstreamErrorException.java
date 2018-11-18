package se.krka.sc2stats;

public class DownstreamErrorException extends RuntimeException {

  public DownstreamErrorException(final String url) {
    super("DownstreamError for url: " + url);
  }
}
