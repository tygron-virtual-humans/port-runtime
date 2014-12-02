/**
 * GOAL interpreter that facilitates developing and executing GOAL multi-agent
 * programs. Copyright (C) 2011 K.V. Hindriks, W. Pasman
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package goal.parser;

import org.antlr.runtime.ClassicToken;

public class LinkedListToken extends ClassicToken {
	private LinkedListToken prev = null;
	private LinkedListToken next = null;

	public LinkedListToken(int type, String text) {
		super(type, text);
	}

	public LinkedListToken getNext() {
		return next;
	}

	public void setNext(LinkedListToken next) {
		if (this == next) {
			throw new IllegalArgumentException("Token stream loop detected ("
					+ toString() + ")");
		}
		this.next = next;
		if (next != null) {
			next.prev = this;
		}
	}

	public LinkedListToken getPrev() {
		return prev;
	}

	public void setPrev(LinkedListToken prev) {
		if (this == prev) {
			throw new IllegalArgumentException("Token stream loop detected");
		}
		this.prev = prev;
		if (prev != null) {
			prev.next = this;
		}
	}

	public void afterInsert(LinkedListToken insert) {
		if (insert.getPrev() != null) {
			throw new IllegalArgumentException("afterInsert(" + insert
					+ ") : prev was not null");
		}
		if (insert.getNext() != null) {
			throw new IllegalArgumentException("afterInsert(" + insert
					+ ") : next was not null");
		}
		insert.next = next;
		insert.prev = this;
		if (next != null) {
			next.prev = insert;
		}
		next = insert;
	}

	public void beforeInsert(LinkedListToken insert) {
		if (insert.getPrev() != null) {
			throw new IllegalArgumentException("beforeInsert(" + insert
					+ ") : prev was not null");
		}
		if (insert.getNext() != null) {
			throw new IllegalArgumentException("beforeInsert(" + insert
					+ ") : next was not null");
		}
		insert.prev = prev;
		insert.next = this;
		if (prev != null) {
			prev.next = insert;
		}
		prev = insert;
	}

	public void delete() {
		if (prev != null) {
			prev.next = next;
		}
		if (next != null) {
			next.prev = prev;
		}
		next = prev = null;
	}
}
