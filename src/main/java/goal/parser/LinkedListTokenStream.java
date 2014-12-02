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

/*
 * Copyright (c) 2006 David Holroyd
 */

import org.antlr.runtime.CharStream;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenSource;
import org.antlr.runtime.TokenStream;

public class LinkedListTokenStream implements TokenStream {

	private TokenSource tokenSource;
	private LinkedListToken head;
	private LinkedListToken tail;

	/**
	 * Skip tokens on any channel but this one; this is how we skip
	 * whitespace...
	 */
	private int channel = Token.DEFAULT_CHANNEL;

	/** By default, track all incoming tokens */
	private boolean discardOffChannelTokens = false;

	/** Track the last mark() call result value for use in rewind(). */
	private LinkedListToken lastMarker;

	/**
	 * The current element in the tokens list (next token to consume). p==null
	 * indicates that the tokens list is empty
	 */
	private LinkedListToken p = null;

	public LinkedListTokenStream() {
	}

	public TokenSource getSource() {
		return tokenSource;
	}

	/**
	 * Reverses the stream 'count' tokens back, causing the tokens to be removed
	 * from the stream. Can be used to erase tokens which parser lookahead has
	 * summoned, but which represent input to be handled by an 'island grammar'.
	 *
	 * @param count
	 *            The amount to scrub
	 */
	public void scrub(int count) {
		if (p == null) {
			p = tail;
		}
		for (; count > 0; count--) {
			p = p.getPrev();
		}
		p.setNext(null);
		tail = p;
		p = null;
	}

	/**
	 * The given TokenSource must produce tokens of type LinkedListToken
	 *
	 * @param tokenSource
	 *            The TokenSource to use
	 */
	public LinkedListTokenStream(TokenSource tokenSource) {
		this.tokenSource = tokenSource;
	}

	/**
	 * The given TokenSource must produce tokens of type LinkedListToken
	 *
	 * @param tokenSource
	 *            The TokenSource to use
	 * @param channel
	 *            A channel to use (DEFAULT is the default)
	 */
	public LinkedListTokenStream(TokenSource tokenSource, int channel) {
		this(tokenSource);
		this.channel = channel;
	}

	/**
	 * Reset this token stream by setting its token source.
	 *
	 * @param tokenSource
	 *            The new TokenSource
	 */
	public void setTokenSource(TokenSource tokenSource) {
		this.tokenSource = tokenSource;
		p = null;
		channel = Token.DEFAULT_CHANNEL;
	}

	private LinkedListToken readNextToken() {
		LinkedListToken t = (LinkedListToken) tokenSource.nextToken();
		while (t != null && t.getType() != CharStream.EOF) {
			boolean discard = false;
			if (discardOffChannelTokens && t.getChannel() != this.channel) {
				discard = true;
			}
			if (!discard) {
				if (head == null && tail == null) {
					head = tail = t;
				} else {
					tail.setNext(t);
					t.setPrev(tail);
					tail = t;
				}
				break;
			}
			t = (LinkedListToken) tokenSource.nextToken();
		}
		if (t.getType() == CharStream.EOF) {
			// prevent ourselves from producing lots of EOF tokens
			// if the parser is 'pushy'; also, do the head/tail dance
			if (tail != null && tail.getType() == CharStream.EOF) {
				return tail;
			} else {
				if (head == null && tail == null) {
					head = tail = t;
				} else {
					tail.setNext(t);
					t.setPrev(tail);
					tail = t;
				}
			}
		}
		return skipOffTokenChannels(t);
	}

	/**
	 * Returns the token that follows the given token in the stream, or null if
	 * there's no token following
	 */
	private LinkedListToken succ(LinkedListToken tok) {
		LinkedListToken next = tok.getNext();
		if (next == null) {
			next = readNextToken();
		}
		return next;
	}

	/**
	 * Return absolute token i; ignore which channel the tokens are on; that is,
	 * count all tokens not just on-channel tokens.
	 */
	@Override
	public Token get(int i) {
		LinkedListToken tok = head;
		for (int c = 0; c < i; c++) {
			tok = succ(tok);
		}
		return tok;
	}

	@Override
	public TokenSource getTokenSource() {
		return tokenSource;
	}

	@Override
	public Token LT(int k) {
		if (p == null) {
			p = readNextToken();
		}
		if (k == 0) {
			return null;
		}
		if (k < 0) {
			return LB(-k);
		}
		LinkedListToken i = p;
		int n = 1;
		// find k good tokens
		while (n < k) {
			LinkedListToken next = succ(i);
			if (i == null) {
				return Token.EOF_TOKEN;
			}
			// skip off-channel tokens
			i = skipOffTokenChannels(next); // leave p on valid token
			n++;
		}
		if (i == null) {
			return Token.EOF_TOKEN;
		}
		return i;
	}

	/** Look backwards k tokens on-channel tokens */
	protected Token LB(int k) {
		if (p == null) {
			p = readNextToken();
		}
		if (k == 0) {
			return null;
		}

		LinkedListToken i = p;
		int n = 1;
		// find k good tokens looking backwards
		while (n <= k) {
			LinkedListToken next = i.getPrev();
			if (next == null) {
				return null;
			}
			// skip off-channel tokens
			i = skipOffTokenChannelsReverse(next); // leave p on valid token
			n++;
		}
		return i;
	}

	@Override
	public String toString(int start, int stop) {
		LinkedListToken tok = head;
		int i = 0;
		for (; i < start && tok != null; i++) {
			tok = succ(tok);
		}
		StringBuffer buf = new StringBuffer();
		for (; i <= stop && tok != null; i++) {
			buf.append(tok.getText());
			tok = succ(tok);
		}
		return buf.toString();
	}

	@Override
	public String toString(Token start, Token stop) {
		LinkedListToken tok = (LinkedListToken) start;
		StringBuffer buf = new StringBuffer();
		do {
			buf.append(tok.getText());
			tok = succ(tok);
		} while (tok != null && tok != stop);
		return buf.toString();
	}

	@Override
	public void consume() {
		do {
			p = p.getNext();
		} while (p != null && p.getChannel() != channel);
	}

	@Override
	public int index() {
		int i = 0;
		for (LinkedListToken tok = head; tok != p && tok != null; tok = tok
				.getNext()) {
			i++;
		}
		return i;
	}

	@Override
	public int LA(int i) {
		return LT(i).getType();
	}

	@Override
	public int mark() {
		// TODO: could store marks in a hash; does it make any difference?
		lastMarker = p;
		return index();
	}

	@Override
	public void release(int marker) {
		// no resources to release
	}

	@Override
	public void rewind() {
		p = lastMarker;
	}

	@Override
	public void rewind(int marker) {
		seek(marker);
	}

	@Override
	public void seek(int index) {
		p = head;
		for (int i = 0; i < index; i++) {
			p = succ(p);
		}
	}

	@Override
	public int size() {
		int s = 0;
		for (LinkedListToken tok = head; tok != null; tok = tok.getNext()) {
			s++;
		}
		return s;
	}

	public void discardOffChannelTokens(boolean discardOffChannelTokens) {
		this.discardOffChannelTokens = discardOffChannelTokens;
	}

	protected LinkedListToken skipOffTokenChannels(LinkedListToken i) {
		while (i != null && i.getChannel() != channel) {
			i = succ(i);
		}
		return i;
	}

	protected LinkedListToken skipOffTokenChannelsReverse(LinkedListToken i) {
		while (i != null && i.getChannel() != channel) {
			i = i.getPrev();
		}
		return i;
	}

	@Override
	public String getSourceName() {
		// TODO Auto-generated method stub
		return null;
	}
}