# HashMap

允许空值和空键，HashMap类大致相当于Hashtable，不同之处在于它是不同步的，并且允许为空。

## 常量

```java
    /**
     * The default initial capacity - MUST be a power of two.
     */
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16

    /**
     * The maximum capacity, used if a higher value is implicitly specified
     * by either of the constructors with arguments.
     * MUST be a power of two <= 1<<30.
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * The load factor used when none specified in constructor.
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * The bin count threshold for using a tree rather than list for a
     * bin.  Bins are converted to trees when adding an element to a
     * bin with at least this many nodes. The value must be greater
     * than 2 and should be at least 8 to mesh with assumptions in
     * tree removal about conversion back to plain bins upon
     * shrinkage.
     * 链表长度超过8才有可能树化
     */
    static final int TREEIFY_THRESHOLD = 8;

    /**
     * The bin count threshold for untreeifying a (split) bin during a
     * resize operation. Should be less than TREEIFY_THRESHOLD, and at
     * most 6 to mesh with shrinkage detection under removal.
     */
    static final int UNTREEIFY_THRESHOLD = 6;

    /**
     * The smallest table capacity for which bins may be treeified.
     * (Otherwise the table is resized if too many nodes in a bin.)
     * Should be at least 4 * TREEIFY_THRESHOLD to avoid conflicts
     * between resizing and treeification thresholds.
     * 数组长度超过64，链表才有可能树化
     */
    static final int MIN_TREEIFY_CAPACITY = 64;
```

数组长度超过64，链表长度超过8，链表才有可能转换为红黑树。当数组长度<64，链表长度>8，数组会扩容，链表的元素会被重新散列。

## 基本结构

```java
    static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        V value;
        Node<K,V> next;

        Node(int hash, K key, V value, Node<K,V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public final K getKey()        { return key; }
        public final V getValue()      { return value; }
        public final String toString() { return key + "=" + value; }

        /** 
         * hashCode()方法通过异或计算，
         * 将key和value的哈希值进行异或操作得到一个新的哈希值作为Entry的哈希值。
         * Entry的哈希值是用来确定它在HashMap中的位置的，
         * 通过这个哈希值可以快速定位HashMap中的元素。
         */
        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        public final V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        public final boolean equals(Object o) {
            if (o == this)
                return true;

            return o instanceof Map.Entry<?, ?> e
                    && Objects.equals(key, e.getKey())
                    && Objects.equals(value, e.getValue());
        }
    }
```

## Static utilities

```java
    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
```

## Fields

```java
    transient Node<K,V>[] table;

    transient Set<Map.Entry<K,V>> entrySet;

    transient int size;

    transient int modCount;

    int threshold;

    final float loadFactor;
```

## Public operations

```java
	// 调用getNode根据key找到键值对，然后返回键值对中的值
	public V get(Object key) {
        Node<K,V> e;
        return (e = getNode(key)) == null ? null : e.value;
    }

	// 根据key找到并返回键值对
    final Node<K,V> getNode(Object key) {
        // tab 表示哈希表数组、first 表示桶中的第一个节点、e 表示链表中的其他节点
        // n 表示哈希表长度、hash 表示给定键的 hash 值、k 表示节点的键
        Node<K,V>[] tab; Node<K,V> first, e; int n, hash; K k;
        // table为hashmap的Node<K,V>[]。
        // 使用 (n - 1) & (hash = hash(key)) 可以将哈希值 hash(key) 限定在哈希表的下标范围内，避免越界访问。
        // n 表示哈希表的长度，因为长度是 2 的整数次幂，使用 n - 1 的结果的二进制表示中所有的位都是 1，
        // 因此将其与哈希值进行按位与操作，可以得到哈希值在哈希表的下标。
        if ((tab = table) != null && (n = tab.length) > 0 &&
            (first = tab[(n - 1) & (hash = hash(key))]) != null) {
            if (first.hash == hash && // always check first node
                ((k = first.key) == key || (key != null && key.equals(k))))
                return first;
            // TreeNode<K,V> extends LinkedHashMap.Entry<K,V>
            // Entry<K,V> extends HashMap.Node<K,V>
            if ((e = first.next) != null) {
                if (first instanceof TreeNode)
                    return ((TreeNode<K,V>)first).getTreeNode(hash, key);
                do {
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        return e;
                } while ((e = e.next) != null);
            }
        }
        return null;
    }
```

