package com.enfernuz.util.stream;

import com.google.common.collect.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;
import java.util.stream.Collector;

import static java.util.Objects.requireNonNull;

/**
 *
 * A collection of various {@code java.util.stream.Collector} factory methods.
 * 
 * Created by A. Nerushev
 */
public final class Collectors {
    
    // A good guide about Collectors:
    // http://www.nurkiewicz.com/2014/07/introduction-to-writing-custom.html
    
    private Collectors() {
        throw new AssertionError("The class is uninstantiable.");
    }

    /**
     * Creates a collector that reduces a stream of elements into an 
     * {@link com.google.common.collect.ImmutableCollection} instance, the type of which depends on 
     * the given {@link com.google.common.collect.ImmutableCollection.Builder} supplier.
     * @param <T> the type of elements in the stream to be reduced by the collector
     * @param immutableCollectionBuilderFactory an immutable collection builder factory to be used
     * to construct the result immutable collection
     * @return a collector that reduces a stream of elements into an immutable collection
     * @throws NullPointerException if the passed argument is a null reference
     */
    public static <T> Collector<T, ?, ImmutableCollection<T>> toImmutableCollection(
            Supplier< ImmutableCollection.Builder<T> > immutableCollectionBuilderFactory) 
            throws NullPointerException {

        requireNonNull(immutableCollectionBuilderFactory);
        
        return Collector.of(
                immutableCollectionBuilderFactory, //supplier
                (builder, t) -> builder.add(t), //accumulator
                (builder1, builder2) -> { builder1.addAll( builder2.build() ); return builder1; }, //combiner
                ImmutableCollection.Builder::<T>build //finisher
        );
    }
    
    /**
     * Creates a collector that reduces a stream of elements into an immutable list.
     * @param <T> the type of elements in the stream to be reduced by the collector
     * @return a collector that reduces a stream of elements into an immutable list
     */
    public static <T> Collector<T, ?, ImmutableList<T>> toImmutableList() {
        
        return Collector.of(
                () -> Collections.synchronizedList( new ArrayList<T>() ), 
                (list, t) -> list.add(t), 
                (list1, list2) -> { list1.addAll(list2); return list1; }, 
                ImmutableList::<T>copyOf, 
                Collector.Characteristics.CONCURRENT
        );
    }
    
    /**
     * Creates a collector that reduces a stream of elements into an immutable set.
     * @param <T> the type of elements in the stream to be reduced by the collector
     * @return a collector that reduces a stream of elements into an immutable set
     */
    public static <T> Collector<T, ?, ImmutableSet<T>> toImmutableSet() {
        
        return Collector.of(
                () -> Collections.synchronizedSet( new HashSet<T>() ), 
                (set, t) -> set.add(t), 
                (set1, set2) -> { set1.addAll(set2); return set1; }, 
                ImmutableSet::<T>copyOf, 
                Collector.Characteristics.CONCURRENT, 
                Collector.Characteristics.UNORDERED
        );
    }
    
    /**
     * Creates a collector that reduces a stream of elements into an immutable sorted set, the 
     * elements of which are sorted using the provided comparator instance.
     * @param <T> the type of elements in the stream to be reduced by the collector
     * @param comparator a comparator to be used for sorting the set
     * @return a collector that reduces a stream of elements into an immutable sorted set
     * @throws NullPointerException if the passed argument is a null reference
     */
    public static <T> Collector<T, ?, ImmutableSortedSet<T>> toImmutableSortedSet(
                Comparator<T> comparator) throws NullPointerException {
        
        requireNonNull(comparator);
       
        return Collector.of(
                () -> Collections.synchronizedSet( new HashSet<T>() ), 
                (set, t) -> set.add(t), 
                (set1, set2) -> { set1.addAll(set2); return set1; }, 
                (set) -> ImmutableSortedSet.<T>copyOf(comparator, set), 
                Collector.Characteristics.CONCURRENT, 
                Collector.Characteristics.UNORDERED
        );
    }
    
    /**
     * Creates a collector that reduces a stream of elements into an immutable map.
     * @param <T> the type of elements in the stream to be reduced by the collector
     * @param <K> the type of the keys of the result map
     * @param <V> the type of the values of the result map
     * @param keyMapper a function to be used to map the elements of the stream to the keys of the
     * result map
     * @param valueMapper a function to be used to map the elements of the stream to the values of 
     * the result map
     * @return a collector that reduces a stream of elements into an immutable map
     * @throws NullPointerException if either of the passed arguments is a null reference
     */
    public static <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(
            Function<? super T, K> keyMapper,
            Function<? super T, V> valueMapper) throws NullPointerException {
        
        requireNonNull(keyMapper);
        requireNonNull(valueMapper);
        
        return Collector.of(
                ConcurrentHashMap<K, V>::new, 
                (map, t) -> map.put(keyMapper.apply(t), valueMapper.apply(t)), 
                (map1, map2) -> { map1.putAll(map2); return map1; }, 
                ImmutableMap::<K, V>copyOf, 
                Collector.Characteristics.CONCURRENT,
                Collector.Characteristics.UNORDERED
        );
    }
    
    /**
     * Creates a collector that reduces a stream of elements into a 
     * {@link com.google.common.collect.ImmutableBiMap} instance.
     * @param <T> the type of elements in the stream to be reduced by the collector
     * @param <K> the type of the keys of the result bimap
     * @param <V> the type of the values of the result bimap
     * @param keyMapper a function to be used to map the elements of the stream to the keys of the
     * result bimap
     * @param valueMapper a function to be used to map the elements of the stream to the values of 
     * the result bimap
     * @return a collector that reduces a stream of elements into an immutable BiMap
     * @throws NullPointerException if either of the passed arguments is a null reference
     */
    public static <T, K, V> Collector<T, ?, ImmutableBiMap<K, V>> toImmutableBiMap(
            Function<? super T, K> keyMapper,
            Function<? super T, V> valueMapper) throws NullPointerException {
        
        requireNonNull(keyMapper);
        requireNonNull(valueMapper);
        
        return Collector.of(
                ConcurrentHashMap<K, V>::new, 
                (map, t) -> map.put(keyMapper.apply(t), valueMapper.apply(t)), 
                (map1, map2) -> { map1.putAll(map2); return map1; }, 
                ImmutableBiMap::<K, V>copyOf, 
                Collector.Characteristics.CONCURRENT, 
                Collector.Characteristics.UNORDERED
        );
    }
    
    /**
     * Creates a collector that reduces a stream of elements into a 
     * {@link com.google.common.collect.ImmutableSortedMap} instance.
     * @param <T> the type of elements in the stream to be reduced by the collector
     * @param <K> the type of the keys of the result map
     * @param <V> the type of the values of the result bimap
     * @param keyMapper a function to be used to map the elements of the stream to the keys of the
     * result map
     * @param valueMapper a function to be used to map the elements of the stream to the values of 
     * the result map
     * @param comparator a comparator to be used for ordering the keys of the result map
     * @return a collector that reduces a stream of elements into an immutable sorted map
     * @throws NullPointerException if either of the passed arguments is a null reference
     */
    public static <T, K, V> Collector<T, ?, ImmutableSortedMap<K, V>> toImmutableSortedMap(
            Function<? super T, K> keyMapper,
            Function<? super T, V> valueMapper,
            Comparator<K> comparator) throws NullPointerException {
        
        requireNonNull(keyMapper);
        requireNonNull(valueMapper);
        requireNonNull(comparator);
        
        return Collector.of(
                ConcurrentHashMap<K, V>::new, 
                (map, t) -> map.put(keyMapper.apply(t), valueMapper.apply(t)), 
                (map1, map2) -> { map1.putAll( map2 ); return map1; }, 
                (map) -> ImmutableSortedMap.copyOf(map, comparator), 
                Collector.Characteristics.CONCURRENT, 
                Collector.Characteristics.UNORDERED
        );
    }
    
    /**
     * Creates a collector that reduces a stream of elements into an 
     * {@link com.google.common.collect.Multimap} instance, the type of which depends on 
     * the given multimap factory.
     * @param <T> the type of elements in the stream to be reduced by the collector
     * @param <K> the type of the keys of the result multimap
     * @param <V> the type of the elements of the result multimap's collection-values
     * @param keyMapper a function to be used to map the elements of the stream to the keys of the
     * result multimap
     * @param valueMapper a function to be used to map the elements of the stream to the values of 
     * the result multimap
     * @param multimapFactory a multimap factory to be used to construct the result multimap
     * @return a collector that reduces a stream of elements into a 
     * {@link com.google.common.collect.Multimap} instance
     * @throws NullPointerException if either of the passed arguments is a null reference
     */
    public static <T, K, V> Collector<T, ?, Multimap<K, V>> toMultimap(
            Function<? super T, K> keyMapper, 
            Function<? super T, V> valueMapper, 
            Supplier<Multimap<K,V>> multimapFactory) throws NullPointerException {
        
        requireNonNull(keyMapper);
        requireNonNull(valueMapper);
        requireNonNull(multimapFactory);
        
        return Collector.of(
                () -> Multimaps.synchronizedMultimap( multimapFactory.get() ), 
                (multimap, t) -> multimap.put( keyMapper.apply(t), valueMapper.apply(t) ), 
                (multimap1, multimap2) -> { multimap1.putAll(multimap2); return multimap1; },
                Collector.Characteristics.CONCURRENT, 
                Collector.Characteristics.IDENTITY_FINISH
        );
    }
    
    /**
     * Creates a collector that reduces a stream of elements into an 
     * {@link com.google.common.collect.ImmutableListMultimap} instance.
     * @param <T> the type of elements in the stream to be reduced by the collector
     * @param <K> the type of the keys of the result multimap
     * @param <V> the type of the elements of the result multimap's collection-values
     * @param keyMapper a function to be used to map the elements of the stream to the keys of the
     * result multimap
     * @param valueMapper a function to be used to map the elements of the stream to the values of 
     * the result multimap
     * @return a collector that reduces a stream of elements into a 
     * {@link com.google.common.collect.ImmutableListMultimap} instance
     * @throws NullPointerException if either of the passed arguments is a null reference
     */
    public static <T, K, V> Collector<T, ?, ImmutableListMultimap<K, V>> toImmutableListMultimap(
            Function<? super T, K> keyMapper,
            Function<? super T, V> valueMapper) throws NullPointerException {
        
        requireNonNull(keyMapper);
        requireNonNull(valueMapper);
        
        return Collector.of(
                () -> Multimaps.synchronizedListMultimap(
                        Multimaps.newListMultimap(new HashMap<K, Collection<V>>(), ArrayList<V>::new)
                ), 
                (listMultimap, t) -> listMultimap.put(keyMapper.apply(t), valueMapper.apply(t)), 
                (listMultimap1, listMultimap2) -> { listMultimap1.putAll( listMultimap2 ); return listMultimap1; }, 
                ImmutableListMultimap::<K, V>copyOf, 
                Collector.Characteristics.CONCURRENT
        );
    }
    
    /**
     * Creates a collector that reduces a stream of elements into an 
     * {@link com.google.common.collect.ImmutableSetMultimap} instance.
     * @param <T> the type of elements in the stream to be reduced by the collector
     * @param <K> the type of the keys of the result multimap
     * @param <V> the type of the elements of the result multimap's collection-values
     * @param keyMapper a function to be used to map the elements of the stream to the keys of the
     * result multimap
     * @param valueMapper a function to be used to map the elements of the stream to the values of 
     * the result multimap
     * @return a collector that reduces a stream of elements into a 
     * {@link com.google.common.collect.ImmutableSetMultimap} instance
     * @throws NullPointerException if either of the passed arguments is a null reference
     */
    public static <T, K, V> Collector<T, ?, ImmutableSetMultimap<K, V>> toImmutableSetMultimap(
            Function<? super T, K> keyMapper,
            Function<? super T, V> valueMapper) throws NullPointerException {
        
        requireNonNull(keyMapper);
        requireNonNull(valueMapper);
        
        return Collector.of(
                () -> Multimaps.synchronizedSetMultimap(
                        Multimaps.newSetMultimap(new HashMap<K, Collection<V>>(), HashSet<V>::new)
                ), 
                (multimap, t) -> multimap.put(keyMapper.apply(t), valueMapper.apply(t)), 
                (multimap1, multimap2) -> { multimap1.putAll( multimap2 ); return multimap1; }, 
                ImmutableSetMultimap::<K, V>copyOf, 
                Collector.Characteristics.CONCURRENT, 
                Collector.Characteristics.UNORDERED
        );
    }
    
    /**
     * Creates a collector that reduces a stream of elements into an 
     * {@link com.google.common.collect.Table} instance, the type of which depends on the given 
     * table factory.
     * @param <T> the type of elements in the stream to be reduced by the collector
     * @param <R> the type of the table's rows
     * @param <C> the type of the table's columns
     * @param <V> the type of the table's values
     * @param rowMapper a function to be used to map the elements of the stream to the row keys of 
     * the result table
     * @param columnMapper a function to be used to map the elements of the stream to the column 
     * keys of the result table
     * @param valueMapper a function to be used to map the elements of the stream to the values of 
     * the result table
     * @param tableFactory a table factory to be used to construct the result table
     * @return a collector that reduces a stream of elements into a 
     * {@link com.google.common.collect.Table} instance
     * @throws NullPointerException if either of the passed arguments is a null reference
     */
    public static <T, R, C, V> Collector<T, ?, Table<R, C, V>> toTable(
            Function<? super T, R> rowMapper, 
            Function<? super T, C> columnMapper,
            Function<? super T, V> valueMapper,
            Supplier<Table<R, C, V>> tableFactory) throws NullPointerException {
        
        requireNonNull(rowMapper);
        requireNonNull(columnMapper);
        requireNonNull(valueMapper);
        
        return Collector.of(
                tableFactory, 
                (table, t) -> {
                    final R row = rowMapper.apply(t);
                    final C column = columnMapper.apply(t);
                    final V value = valueMapper.apply(t);
                    table.put(row, column, value);
                }, 
                (table1, table2) -> { table1.putAll(table2); return table1; }, 
                Collector.Characteristics.IDENTITY_FINISH
        );
    }
    
    /**
     * Creates a collector that reduces a stream of elements into an 
     * {@link com.google.common.collect.ImmutableTable} view over the table constructed with the 
     * given table factory method.
     * @param <T> the type of elements in the stream to be reduced by the collector
     * @param <R> the type of the table's rows
     * @param <C> the type of the table's columns
     * @param <V> the type of the table's values
     * @param rowMapper a function to be used to map the elements of the stream to the row keys of 
     * the result table
     * @param columnMapper a function to be used to map the elements of the stream to the column 
     * keys of the result table
     * @param valueMapper a function to be used to map the elements of the stream to the values of 
     * the result table
     * @param tableFactory a table factory to be used to construct the result table
     * @return a collector that reduces a stream of elements into a 
     * {@link com.google.common.collect.ImmutableTable} instance
     * @throws NullPointerException if either of the passed arguments is a null reference
     */
    public static <T, R, C, V> Collector<T, ?, ImmutableTable<R, C, V>> toImmutableTable(
            Function<? super T, R> rowMapper, 
            Function<? super T, C> columnMapper,
            Function<? super T, V> valueMapper,
            Supplier<Table<R, C, V>> tableFactory) throws NullPointerException {
        
        requireNonNull(rowMapper);
        requireNonNull(columnMapper);
        requireNonNull(valueMapper);
        
        return Collector.of(
                tableFactory, 
                (table, t) -> {
                    final R row = rowMapper.apply(t);
                    final C column = columnMapper.apply(t);
                    final V value = valueMapper.apply(t);
                    table.put(row, column, value);
                }, 
                (table1, table2) -> { table1.putAll(table2); return table1; }, 
                ImmutableTable::<R, C, V>copyOf 
        );
    }
    
}