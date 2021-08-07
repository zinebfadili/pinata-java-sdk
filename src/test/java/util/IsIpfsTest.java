package util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class IsIpfsTest {

  @ParameterizedTest
  @ValueSource(strings = {
      "/p2p-circuit",
      "/p2p-circuit/ipfs/QmUjNmr8TgJCn1Ao7DvMy4cjoZU15b9bwSCBLE3vwXiwgj",
      "/p2p-circuit/p2p/QmUjNmr8TgJCn1Ao7DvMy4cjoZU15b9bwSCBLE3vwXiwgj",
      "/p2p-circuit/ip4/127.0.0.1/tcp/20008/ws/ipfs/QmUjNmr8TgJCn1Ao7DvMy4cjoZU15b9bwSCBLE3vwXiwgj",
      "/p2p-circuit/ip4/127.0.0.1/tcp/20008/ws/p2p/QmUjNmr8TgJCn1Ao7DvMy4cjoZU15b9bwSCBLE3vwXiwgj",
      "/p2p-circuit/ip4/1.2.3.4/tcp/3456/p2p/QmcgpsyWgH8Y8ajJz1Cu72KnS5uo2Aa2LpzU7kinSoooo4",
      "/p2p-circuit/ip4/1.2.3.4/tcp/3456/ipfs/QmcgpsyWgH8Y8ajJz1Cu72KnS5uo2Aa2LpzU7kinSoooo4",
      "/p2p-circuit/ip4/127.0.0.1/tcp/4002/ipfs/QmddWMcQX6orJGHpETYMyPgXrCXCtYANMFVDCvhKoDwLqA",
      "/p2p-circuit/ipfs/QmddWMcQX6orJGHpETYMyPgXrCXCtYANMFVDCvhKoDwLqA",
      "/p2p-circuit/p2p/QmddWMcQX6orJGHpETYMyPgXrCXCtYANMFVDCvhKoDwLqA",
      "/p2p-circuit/ip4/127.0.0.1/tcp/20008/ws/ipfs/QmUjNmr8TgJCn1Ao7DvMy4cjoZU15b9bwSCBLE3vwXiwgj/" +
          "p2p-circuit/ipfs/QmUjNmr8TgJCn1Ao7DvMy4cjoZU15b9bwSCBLE3vwXiwgj",
      "/ip4/127.0.0.1/tcp/20008/ws/ipfs/QmUjNmr8TgJCn1Ao7DvMy4cjoZU15b9bwSCBLE3vwXiwgj",
      "/ip4/127.0.0.1/tcp/20008/ws/p2p/QmUjNmr8TgJCn1Ao7DvMy4cjoZU15b9bwSCBLE3vwXiwgj",
      "/ip4/1.2.3.4/tcp/3456/ipfs/QmcgpsyWgH8Y8ajJz1Cu72KnS5uo2Aa2LpzU7kinSoooo4",
      "/dnsaddr/bootstrap.libp2p.io/p2p/QmNnooDu7bfjPFoTZYxMNLWUQJyrVwtbZg5gBMjTezGAJN",
      "/ipfs/QmcgpsyWgH8Y8ajJz1Cu72KnS5uo2Aa2LpzU7kinSoooo4/p2p-circuit",
      "/ipfs/QmcgpsyWgH8Y8ajJz1Cu72KnS5uo2Aa2LpzU7kinSoooo4/p2p-circuit/ipfs/QmUjNmr8TgJCn1Ao7DvMy4cjoZU15b9bwSCBLE3vwXiwgj"
  })
  public void validPeerMultiAddr(String peerMultiAddr) {
    assertTrue(IsIpfs.isPeerMultiAddr(peerMultiAddr));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "ipfs/QmcgpsyWgH8Y8ajJz1Cu72KnS5uo2Aa2LpzU7kinSoooo4",
      "/dnsaddr/bootstrap.libp2p.io",
      "/yoloinvalid/1.2.3.4/tcp/3456/ipfs/QmcgpsyWgH8Y8ajJz1Cu72KnS5uo2Aa2LpzU7kinSoooo4",
      "noop",
  })
  public void invvalidPeerMultiAddr(String peerMultiAddr) {
    assertFalse(IsIpfs.isPeerMultiAddr(peerMultiAddr));
  }

}
