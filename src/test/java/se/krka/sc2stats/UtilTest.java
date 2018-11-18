package se.krka.sc2stats;

import static com.google.common.collect.ImmutableList.of;
import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Function;
import org.junit.Test;

public class UtilTest {

  @Test
  public void testSimplePartition() {
    List<List<Integer>> actual = Util.partitionByKey(of(1, 2, 3), 1, 1, Function.identity());
    ImmutableList<ImmutableList<Integer>> expected = of(of(1), of(2), of(3));
    assertEquals(expected, actual);
  }

  @Test
  public void testPartition2() {
    List<List<Integer>> actual = Util.partitionByKey(of(1, 2, 3, 4, 5, 5, 5, 6, 7, 8), 1, 3, Function.identity());
    ImmutableList<ImmutableList<Integer>> expected = of(of(1, 2, 3), of(4, 5, 5, 5, 6), of(7, 8));
    assertEquals(expected, actual);
  }
}