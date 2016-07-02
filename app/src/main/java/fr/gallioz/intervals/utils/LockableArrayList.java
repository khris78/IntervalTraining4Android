package fr.gallioz.intervals.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class LockableArrayList<T> implements List<T> {

    private static class LockableIterator<T> implements Iterator<T> {

        private LockableArrayList<T> theArray;
        private Iterator<T> i;

        public LockableIterator(LockableArrayList<T> array) {
            this.theArray = array;
            i = theArray.a.iterator();
        }

        @Override
        public boolean hasNext() {
            return i.hasNext();
        }

        @Override
        public T next() {
            return i.next();
        }

        @Override
        public void remove() {
            if (theArray.isLocked()) {
                throw new IllegalStateException("List is locked");
            } else {
                i.remove();
            }
        }
    }

    private static class LockableListIterator<T> implements ListIterator<T> {

        private LockableArrayList<T> theArray;
        private ListIterator<T> i;

        public LockableListIterator(LockableArrayList<T> array) {
            this.theArray = array;
            i = theArray.a.listIterator();
        }

        public LockableListIterator(LockableArrayList<T> array, int location) {
            this.theArray = array;
            i = theArray.a.listIterator(location);
        }

        @Override
        public void add(T object) {
            if (theArray.isLocked()) {
                throw new IllegalStateException("List is locked");
            } else {
                i.add(object);
            }
        }

        @Override
        public boolean hasNext() {
            return i.hasNext();
        }

        @Override
        public boolean hasPrevious() {
            return i.hasPrevious();
        }

        @Override
        public T next() {
            return i.next();
        }

        @Override
        public int nextIndex() {
            return i.nextIndex();
        }

        @Override
        public T previous() {
            return i.previous();
        }

        @Override
        public int previousIndex() {
            return i.previousIndex();
        }

        @Override
        public void remove() {
            if (theArray.isLocked()) {
                throw new IllegalStateException("List is locked");
            } else {
                i.remove();
            }
        }

        @Override
        public void set(T object) {
            if (theArray.isLocked()) {
                throw new IllegalStateException("List is locked");
            } else {
                i.set(object);
            }
        }
    }

    private ArrayList<T> a;
    private boolean isLocked;

    public LockableArrayList() {
        a = new ArrayList<>();
    }

    public LockableArrayList(int capacity) {
        a = new ArrayList<>(capacity);
    }

    public LockableArrayList(Collection<? extends T> collection) {
        a = new ArrayList<>(collection);
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void lock() {
        isLocked = true;
    }

    public void unlock() {
        isLocked = false;
    }

    @Override
    public void add(int location, T object) {
         if (isLocked) {
             throw new IllegalStateException("List is locked");
         } else {
             a.add(location, object);
         }
    }

    @Override
    public boolean add(T object) {
        if (isLocked) {
            throw new IllegalStateException("List is locked");
        } else {
            return a.add(object);
        }
    }

    @Override
    public boolean addAll(int location, Collection<? extends T> collection) {
        if (isLocked) {
            throw new IllegalStateException("List is locked");
        } else {
            return a.addAll(location, collection);
        }
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        if (isLocked) {
            throw new IllegalStateException("List is locked");
        } else {
            return a.addAll(collection);
        }
    }

    @Override
    public void clear() {
        if (isLocked) {
            throw new IllegalStateException("List is locked");
        } else {
            a.clear();
        }
    }

    @Override
    public boolean contains(Object object) {
        return a.contains(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return a.containsAll(collection);
    }

    @Override
    public T get(int location) {
        return a.get(location);
    }

    @Override
    public int indexOf(Object object) {
        return a.indexOf(object);
    }

    @Override
    public boolean isEmpty() {
        return a.isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return new LockableIterator<>(this);
    }

    @Override
    public int lastIndexOf(Object object) {
        return a.lastIndexOf(object);
    }

    @Override
    public ListIterator<T> listIterator() {
        return new LockableListIterator<T>(this);
    }

    @Override
    public ListIterator<T> listIterator(int location) {
        return new LockableListIterator<T>(this, location);
    }

    @Override
    public T remove(int location) {
        if (isLocked) {
            throw new IllegalStateException("List is locked");
        } else {
            return a.remove(location);
        }
    }

    @Override
    public boolean remove(Object object) {
        if (isLocked) {
            throw new IllegalStateException("List is locked");
        } else {
            return a.remove(object);
        }
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        if (isLocked) {
            throw new IllegalStateException("List is locked");
        } else {
            return a.removeAll(collection);
        }
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        if (isLocked) {
            throw new IllegalStateException("List is locked");
        } else {
            return a.retainAll(collection);
        }
    }

    @Override
    public T set(int location, T object) {
        if (isLocked) {
            throw new IllegalStateException("List is locked");
        } else {
            return a.set(location, object);
        }
    }

    @Override
    public int size() {
        return a.size();
    }

    @Override
    public List<T> subList(int start, int end) {
        return a.subList(start, end);
    }

    @Override
    public Object[] toArray() {
        return a.toArray();
    }

    @Override
    public <T> T[] toArray(T[] array) {
        return a.toArray(array);
    }
}
