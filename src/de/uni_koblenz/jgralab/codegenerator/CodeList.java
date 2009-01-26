/*
 * JGraLab - The Java graph laboratory
 * (c) 2006-2008 Institute for Software Technology
 *               University of Koblenz-Landau, Germany
 *
 *               ist@uni-koblenz.de
 *
 * Please report bugs to http://serres.uni-koblenz.de/bugzilla
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package de.uni_koblenz.jgralab.codegenerator;

import java.util.Vector;

/**
 * TODO add comment
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class CodeList extends CodeBlock {
	Vector<CodeBlock> blocks;

	/**
	 * creates an empty <code>CodeList</code>
	 */
	public CodeList() {
		this(null);
	}

	/**
	 * creates a <code>CodeList</code> with an empty vector of CodeBlocks and
	 * a CodeBlock containing <code>parent</code>
	 * @param parent
	 */
	public CodeList(CodeList parent) {
		super(parent);
		blocks = new Vector<CodeBlock>();
	}

	/**
	 * 
	 * @param block
	 */
	public void remove(CodeBlock block) {
		blocks.remove(block);
		block.setParent(null);
	}

	/**
	 * 
	 * @param block
	 */
	public void add(CodeBlock block) {
		add(block, 0);
	}

	/**
	 * 
	 * @param block
	 * @param addIndent
	 */
	public void add(CodeBlock block, int addIndent) {
		if (block == null) {
			return;
		}
		block.additionalIndent = addIndent;
		if (block.getParent() != null) {
			block.getParent().remove(block);
		}
		blocks.add(block);
		block.setParent(this);
	}

	/**
	 * 
	 * @param block
	 */
	public void addNoIndent(CodeBlock block) {
		if (block != null) {
			add(block);
			block.additionalIndent = -1;
		}
	}

	/**
	 * 
	 */
	@Override
	public String getCode(int indentLevel) {
		StringBuilder buf = new StringBuilder();
		for (CodeBlock block : blocks) {
			buf.append(block.getCode(block.additionalIndent + indentLevel + 1));
		}
		return buf.toString();
	}

	/**
	 * empties <code>this</code>
	 */
	@Override
	public void clear() {
		blocks.clear();
	}

	/**
	 * @return the number of elements, which are in all the blocks of which 
	 * <code>this</code> consists
	 */
	@Override
	public int size() {
		int result = 0;
		for (CodeBlock block : blocks) {
			result += block.size();
		}
		return result;
	}
}
