package util;

import io.ipfs.cid.Cid;
import io.ipfs.multiaddr.MultiAddress;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class IsIpfs {

  private IsIpfs() throws IllegalAccessException {
    throw new IllegalAccessException("IsIpfs class");
  }

  public static boolean isCid(String hash) {
    try {
      Cid.decode(hash);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public static boolean isPeerMultiAddr(String addr) {
    if (StringUtils.isBlank(addr)) {
      return false;
    }
    try {
      new MultiAddress(addr);

      List<String> parts = Arrays.asList(addr.split("/"));

      String lastPart = parts.get(parts.size() - 1);
      if (!lastPart.equals("p2p-circuit")) {
        return isCid(lastPart);
      } else {
        if (parts.size() == 2) {
          return true;
        }
      }

      for (String part : parts) {
        if (isCid(part)) {
          return true;
        }
      }

      return false;
    } catch (Exception e) {
      return false;
    }
  }
}
