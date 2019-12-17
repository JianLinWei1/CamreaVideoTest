package sample;

import java.util.LinkedList;
import java.util.Queue;

public class LimitQueue<E> {

	private int limit;

	private Queue<E> queue = new LinkedList<E>();

	public LimitQueue(int limit) {
		this.limit = limit;
	}

	/**
	 * ���У������д�С����ʱ���Ѷ�ͷ��Ԫ��poll��
	 */
	public synchronized void offer(E e) {
		if (queue.size() >= limit) {
			queue.poll();
		}
		queue.offer(e);
	}
	
	public synchronized E poll() {
		return queue.poll();
	}

	public int getLimit() {
		return limit;
	}

	public int size() {
		return queue.size();
	}

}