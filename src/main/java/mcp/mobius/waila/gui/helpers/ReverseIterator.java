package mcp.mobius.waila.gui.helpers;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ReverseIterator<T> implements Iterable<T> {

    private ListIterator<T> listIterator;

    public ReverseIterator(final Collection<T> wrappedList) {
        final List list = Lists.newArrayList(wrappedList);
        this.listIterator = list.listIterator(wrappedList.size());
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {

            public boolean hasNext() {
                return listIterator.hasPrevious();
            }

            public T next() {
                return listIterator.previous();
            }

            public void remove() {
                listIterator.remove();
            }

        };
    }

}
