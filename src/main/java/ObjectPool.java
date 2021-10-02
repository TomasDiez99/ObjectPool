public interface ObjectPool<E> {
    E getItem();
    void disposeItem(E item);
}
