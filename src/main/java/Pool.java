import java.util.*;

public class Pool<E> implements ObjectPool<E> {
    private final ObjectCreator<E> creator;
    private final ObjectInitializer<E> initializer;
    private final ObjectDisposer<E> disposer;
    private final int lowerLimit;
    private final int upperLimit;

    private Collection<E> memory;

    public Pool(ObjectCreator<E> creator, ObjectInitializer<E> initializer, ObjectDisposer<E> disposer, int lowerLimit, int upperLimit) {
        this.creator = creator;
        this.initializer = initializer;
        this.disposer = disposer;
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;

        memory = new HashSet<>();
    }

    @Override
    public E getItem() {
        ensureSize();
        Iterator<E> it = memory.iterator();
        E item = it.next();
        it.remove();
        initializer.initialize(item);

        return item;
    }

    private void ensureSize() {
        if (memory.size() < lowerLimit) {
            while (memory.size() < upperLimit) {
                E item = creator.instantiate();
                memory.add(item);
            }
        }
    }

    @Override
    public void disposeItem(E item) {
        if (!memory.contains(item)) {
            disposer.dispose(item);
            memory.add(item);
        }
    }
}
