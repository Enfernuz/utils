package com.enfernuz.util;

import java.util.Iterator;

import com.google.common.collect.AbstractIterator;

import static com.google.common.base.Preconditions.checkArgument;

import static java.util.Objects.requireNonNull;

/**
 *
 * The iterator which skips null values.
 *
 * Created by A. Nerushev on Mar 13, 2016
 */
public final class SkipNullsIterator<T> extends AbstractIterator<T> {
    
    private final Iterator<? extends T> nullFriendlyIterator;
    
    /**
     * Constructs a null-skipping iterator instance based on a possible null-friendly iterator.
     * @param nullFriendlyIterator an iterator instance based on which a null-skipping iterator will 
     * be created.
     * @throws NullPointerException if the passed argument is a null reference
     */
    public SkipNullsIterator(Iterator<? extends T> nullFriendlyIterator) throws NullPointerException {
        
        super();

        this.nullFriendlyIterator = requireNonNull(nullFriendlyIterator);
    }
    
    /**
     * Constructs a null-skipping iterator instance over an iterable instance.
     * @param nullFriendlyIterable an iterable instance
     * @throws NullPointerException if the passed argument is a null reference
     * @throws IllegalArgumentException if the iterator of the passed iterable argument is null
     */
    public SkipNullsIterator(Iterable<? extends T> nullFriendlyIterable) 
            throws NullPointerException, IllegalArgumentException {
        
        super();
        
        final Iterator<? extends T> iterator = requireNonNull(nullFriendlyIterable).iterator();
        checkArgument(iterator != null, "The iterator of the iterable parameter must not be null.");

        this.nullFriendlyIterator = iterator;
    }

    @Override
    protected T computeNext() {
        
        while ( nullFriendlyIterator.hasNext() ) {
            
            final T next = nullFriendlyIterator.next();
            if (next != null) {
                return next;
            }
        }
        
        return endOfData();
    }

}
