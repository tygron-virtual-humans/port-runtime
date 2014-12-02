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

import org.antlr.runtime.Token;
import org.antlr.runtime.TokenSource;

public class LinkedListTokenSource implements TokenSource {
	private TokenSource delegate;
	private LinkedListToken last = null;

	public LinkedListTokenSource(TokenSource delegate) {
		this.delegate = delegate;
	}

	@Override
	public Token nextToken() {
		LinkedListToken curr = createToken(delegate.nextToken());
		if (last != null) {
			last.setNext(curr);
		}
		curr.setPrev(last);
		last = curr;
		return curr;
	}

	private LinkedListToken createToken(Token tok) {
		LinkedListToken result = new LinkedListToken(tok.getType(),
				tok.getText());
		result.setLine(tok.getLine());
		result.setCharPositionInLine(tok.getCharPositionInLine());
		result.setChannel(tok.getChannel());
		result.setTokenIndex(tok.getTokenIndex());
		return result;
	}

	/**
	 * Redefines the TokenSource to which this object delagates the task of
	 * token creation. This can be used to switch Lexers when an island grammar
	 * is required, for instance.
	 *
	 * @param delegate
	 *            The delegate
	 */
	public void setDelegate(TokenSource delegate) {
		this.delegate = delegate;
	}

	/**
	 * Overrides the 'last' token which this object is remembering in order to
	 * build next/previous links.
	 *
	 * @param tok
	 *            The token
	 */
	public void setLast(LinkedListToken tok) {
		this.last = tok;
	}

	@Override
	public String getSourceName() {
		return delegate.getSourceName();
	}
}