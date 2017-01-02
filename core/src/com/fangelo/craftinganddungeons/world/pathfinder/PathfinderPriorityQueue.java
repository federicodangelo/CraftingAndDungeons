package com.fangelo.craftinganddungeons.world.pathfinder;


//Optimized version of PriorityQueue used by the pathfinder, the biggest difference is that we don't need
//the "entryPos" Dictionary because we are going to store the position in the array inside the PathfinderCell
public class PathfinderPriorityQueue {
	
	static private boolean DO_VALIDATIONS = false;
	
    static public class PriorityQueueEntry
    {
        public float priority;
        public PathfinderCell value;

        public PriorityQueueEntry (float priority, PathfinderCell value)
        {
            this.priority = priority;
            this.value = value;
        }
    }

    private PriorityQueueEntry[] keyValuePool;
    private int poolCount;

    private PriorityQueueEntry[] queue;
    private int count;

    public PathfinderPriorityQueue(int maxSize)
    {
        queue = new PriorityQueueEntry[maxSize];

        keyValuePool = new PriorityQueueEntry[maxSize];
        for (int i = 0; i < maxSize; i++)
            keyValuePool[i] = new PriorityQueueEntry(0, null);
        poolCount = maxSize;
    }

    public int getCount() { 
        return count;
    }

    public int getCapacity() {
        return queue.length;
    }

    public boolean isEmpty () {
        return count == 0;
    }

    public boolean isFull() {
        return count == queue.length;
    }

    /// <summary>
    /// Operation: O(1)
    /// </summary>
    public void clear () {
        for (int i = count - 1; i >= 0; i--)
            returnKeyValueEntry(queue[i]);
        count = 0;
    }

    /// <summary>
    /// Operation: O(1)
    /// </summary>
    public boolean contains(PathfinderCell element) {
        return element.priorityQueuePosition >= 0;
    }

    /// <summary>
    /// Operation: O(1)
    /// </summary>
    public float peekPriority () {
        if (isEmpty())
            return 0;

        return queue[0].priority;
    }

    /// <summary>
    /// Operation: O(1)
    /// </summary>
    public PathfinderCell peek () {
        if (isEmpty())
            return null;

        return queue[0].value;
    }

    /// <summary>
    /// If the queue is empty we do just return null
    /// 
    /// Operation: O(log n)
    /// </summary>
    public PathfinderCell dequeue () {
        if (isEmpty()) 
            return null;

        PriorityQueueEntry result = queue[0];

        queue[0] = queue[count-1];
        removePosition(result.value);
        updatePosition(queue[0].value, 0);
        queue[count-1] = null;
        count --;
        bubbleDown(0);

        PathfinderCell value = result.value;

        returnKeyValueEntry(result);

        return value;
    }

    /// <summary>
    /// Operation: O(log n)
    /// 
    /// InvalidOperationException: If we add an element which is alread in the Queue
    /// </summary>
    public void Enqueue (PathfinderCell element, float priority) {
        if (DO_VALIDATIONS) {
	        if (contains (element)) {
	            throw new RuntimeException("Cannot add an element which is already in the queue");
	        }
        }

        queue[count] = newKeyValueEntry(priority, element);
        updatePosition(element, count);
        count++;

        bubbleUp(count-1);
    }

    /// <summary>
    /// Operation: O(log n)
    /// 
    /// InvalidOperationException: If we want to update the priority of an element which is not in the queue
    /// </summary>
    public void UpdatePriority (PathfinderCell element, float newPrio) {
        if (DO_VALIDATIONS) {
	        if (!contains(element)) {
	            throw new RuntimeException ("Cannot update the priority of an element which is not in the queue");
	        }
        }

        int pos = element.priorityQueuePosition;
        float oldPrio = queue [pos].priority;

        if (oldPrio == newPrio)
            return;

        queue [pos].priority = newPrio;

        if (oldPrio < newPrio)
            bubbleDown(pos);
        else
            bubbleUp(pos);
    }

    /// <summary>
    /// Moving root to correct location
    /// </summary>
    private void bubbleDown (int i) {
        int minPos;
        PriorityQueueEntry min;
        int left;
        int right;

        while (true) {
            minPos = i;
            min = queue[i];
            left = 2 * i + 1;
            right = 2 * i + 2;

            if (left < count && queue[left].priority < min.priority) {
                minPos = left;
                min = queue[left];
            }

            if (right < count && queue[right].priority < min.priority) {
                minPos = right;
                min = queue[right];
            }

            if (min == queue[i]) {
                break;
            } else {
                // swap
                queue[minPos] = queue[i];
                queue[i] = min;

                updatePosition(queue[minPos].value, minPos);
                updatePosition(queue[i].value, i);

                i = minPos;
            }
        }
    }

    /// <summary>
    /// Moving last element of last level to correct location
    /// </summary>
    private void bubbleUp (int i) {
        int n = i;
        int up = (n - 1) / 2;

        while (up >= 0 && queue[up].priority > queue[n].priority) {
            PriorityQueueEntry entry = queue[up];
            queue[up] = queue[n];
            queue[n] = entry;

            updatePosition(queue[n].value, n);
            updatePosition(queue[up].value, up);

            n = up;
            up = (up-1)/2;
        }
    }

    static private void updatePosition (PathfinderCell element, int pos) {
        element.priorityQueuePosition = pos;
    }

    static private void removePosition (PathfinderCell element) {
        element.priorityQueuePosition = -1;
    }

    private void returnKeyValueEntry(PriorityQueueEntry entry)
    {
        if (DO_VALIDATIONS) {
        	for (int i = 0; i < poolCount; i++)
        		if (keyValuePool[i] == entry)
        			throw new RuntimeException("ReturnKeyValueEntry: duplicated return");
        }

        keyValuePool[poolCount++] = entry;
    }

    private PriorityQueueEntry newKeyValueEntry(float key, PathfinderCell value) {
        poolCount--;

        PriorityQueueEntry entry = keyValuePool[poolCount];
        keyValuePool[poolCount] = null;

        entry.priority = key;
        entry.value = value;

        return entry;
    }
}
