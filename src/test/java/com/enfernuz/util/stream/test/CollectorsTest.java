package com.enfernuz.util.stream.test;

import com.google.common.collect.*;

import java.util.*;
import java.util.function.Function;

import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.enfernuz.util.stream.Collectors;

import static java.util.stream.Collectors.*;

import static org.junit.Assert.*;

/**
 *
 * Created by A. Nerushev
 */

@RunWith(JUnit4.class)
public class CollectorsTest {
    
    @Rule
    public ExpectedException thrown;
    
    private static final ImmutableCollection<String> STRINGS = 
            ImmutableList.of("a", "abcde", "abc", "abcdef", "ab");
    
    @Before
    public void setup() {
        thrown = ExpectedException.none();
    }
    
    @After
    public void tearDown() {
        thrown = null;
    }
    
    @Test
    public void testImmutableSetCollector() {
        
        final Set<String> set = STRINGS.parallelStream().collect( toSet() );
        final ImmutableSet<String> immutableSet = 
                STRINGS.parallelStream().collect( Collectors.toImmutableSet() );
        
        assertEquals(set, immutableSet);
    }
    
    @Test
    public void testImmutableSortedSetCollector() {
        
        final Comparator<String> comparator = Comparator.<String>naturalOrder();
        
        final SortedSet<String> sortedSet = new TreeSet<>(comparator);
        STRINGS.stream().forEach(str -> sortedSet.add(str));
        
        final ImmutableSet<String> immutableSortedSet = 
                STRINGS.parallelStream().collect( Collectors.toImmutableSortedSet(comparator) );
        
        assertEquals(sortedSet, immutableSortedSet);
    }
    
    @Test
    public void testImmutableListCollector() {
        
        final ImmutableList<String> immutableList = 
                STRINGS.parallelStream().collect( Collectors.toImmutableList() );
        
        assertEquals(STRINGS, immutableList);
    }
    
    @Test
    public void testImmutableBiMapCollector() {
        
        final BiMap<String, Integer> biMap = HashBiMap.create(5);
        STRINGS.stream().forEach(str -> biMap.put(str, str.length()));
        
        final ImmutableBiMap<String, Integer> immutableBiMap = 
                STRINGS.parallelStream().collect(
                        Collectors.toImmutableBiMap(Function.identity(), String::length)
                );
        
        assertEquals(biMap, immutableBiMap);
    }

    @Test
    public void testImmutableMapCollector() {

        final Map<String, Integer> map = 
                STRINGS.parallelStream().collect( toMap(Function.identity(), String::length) );
        
        final ImmutableMap<String, Integer> immutableMap = 
                STRINGS.parallelStream().collect(
                        Collectors.toImmutableMap(Function.identity(), String::length)
                );
        
        assertEquals(map, immutableMap);
    }
    
    @Test
    public void testImmutableSortedMapCollector() {
        
        final Comparator<String> comparator = Comparator.<String>naturalOrder();
    
        final ImmutableSortedMap<String, Integer> immutableSortedMap = 
                STRINGS.parallelStream().collect(
                        Collectors.toImmutableSortedMap(
                                Function.identity(), 
                                String::length, 
                                comparator
                        )
                );
        
        final SortedMap<String, Integer> sortedMap = new TreeMap<>(comparator);
        STRINGS.stream()
                .forEach( str -> sortedMap.put(str, str.length()) );
        
        assertEquals(sortedMap, immutableSortedMap);
    }
}
