package org.jglue.totorom;

import java.util.Iterator;
import java.util.List;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Predicate;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.Tokens;
import com.tinkerpop.pipes.PipeFunction;

public interface FramedTraversal<S, E> {

	public FramedVertexTraversal<S, E> V();

	public FramedEdgeTraversal<S, E> E();

	/**
	 * Traversal over a of vertices in the graph.
	 * 
	 * @param ids
	 *            The ids of the vertices.
	 * @return The traversal.
	 */
	public FramedVertexTraversal<S, E> v(Object... ids);

	/**
	 * Traversal over a list of edges in the graph.
	 * 
	 * @param ids
	 *            The ids of the edges.
	 * @return The traversal.
	 */
	public FramedEdgeTraversal<S, E> e(Object... ids);

	
	/**
	 * Add an identity step.
	 * 
	 * @return The traversal.
	 */
	public FramedTraversal<S, E> identity();

<<<<<<< /usr/src/app/output/syncleus/ferma/b273f174b303afbba124b948e959de1f0b4ea1d4/totorom-tinkerpop2/src/main/java/org/jglue/totorom/FramedTraversal.java/left.java
	public long count();
	
||||||| /usr/src/app/output/syncleus/ferma/b273f174b303afbba124b948e959de1f0b4ea1d4/totorom-tinkerpop2/src/main/java/org/jglue/totorom/FramedTraversal.java/base.java
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tinkerpop.gremlin.java.GremlinPipeline#out(int,
	 * java.lang.String[])
	 */
	@Override
	public FramedTraversal<S, Vertex> out(final int branchFactor,
			final String... labels) {
		return (FramedTraversal<S, Vertex>) super.out(branchFactor, labels);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tinkerpop.gremlin.java.GremlinPipeline#out(java.lang.String[])
	 */
	@Override
	public FramedTraversal<S, Vertex> out(final String... labels) {
		return (FramedTraversal<S, Vertex>) super.out(labels);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tinkerpop.gremlin.java.GremlinPipeline#in(int,
	 * java.lang.String[])
	 */
	@Override
	public FramedTraversal<S, Vertex> in(final int branchFactor,
			final String... labels) {
		return (FramedTraversal<S, Vertex>) super.in(branchFactor, labels);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tinkerpop.gremlin.java.GremlinPipeline#in(java.lang.String[])
	 */
	@Override
	public FramedTraversal<S, Vertex> in(final String... labels) {
		return (FramedTraversal<S, Vertex>) super.in(labels);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tinkerpop.gremlin.java.GremlinPipeline#both(int,
	 * java.lang.String[])
	 */
	@Override
	public FramedTraversal<S, Vertex> both(final int branchFactor,
			final String... labels) {
		return (FramedTraversal<S, Vertex>) super.both(branchFactor, labels);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tinkerpop.gremlin.java.GremlinPipeline#both(java.lang.String[])
	 */
	@Override
	public FramedTraversal<S, Vertex> both(final String... labels) {
		return (FramedTraversal<S, Vertex>) super.both(labels);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tinkerpop.gremlin.java.GremlinPipeline#outE(int,
	 * java.lang.String[])
	 */
	@Override
	public FramedTraversal<S, Edge> outE(final int branchFactor,
			final String... labels) {
		return (FramedTraversal<S, Edge>) super.outE(branchFactor, labels);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tinkerpop.gremlin.java.GremlinPipeline#outE(java.lang.String[])
	 */
	@Override
	public FramedTraversal<S, Edge> outE(final String... labels) {
		return (FramedTraversal<S, Edge>) super.outE(labels);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tinkerpop.gremlin.java.GremlinPipeline#inE(int,
	 * java.lang.String[])
	 */
	@Override
	public FramedTraversal<S, Edge> inE(final int branchFactor,
			final String... labels) {
		return (FramedTraversal<S, Edge>) super.inE(branchFactor, labels);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tinkerpop.gremlin.java.GremlinPipeline#inE(java.lang.String[])
	 */
	@Override
	public FramedTraversal<S, Edge> inE(final String... labels) {
		return (FramedTraversal<S, Edge>) super.inE(labels);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tinkerpop.gremlin.java.GremlinPipeline#bothE(int,
	 * java.lang.String[])
	 */
	@Override
	public FramedTraversal<S, Edge> bothE(final int branchFactor,
			final String... labels) {
		return (FramedTraversal<S, Edge>) super.bothE(branchFactor, labels);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tinkerpop.gremlin.java.GremlinPipeline#bothE(java.lang.String[])
	 */
	@Override
	public FramedTraversal<S, Edge> bothE(final String... labels) {
		return (FramedTraversal<S, Edge>) super.bothE(labels);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tinkerpop.gremlin.java.GremlinPipeline#inV()
	 */
	@Override
	public FramedTraversal<S, Vertex> inV() {
		return (FramedTraversal<S, Vertex>) super.inV();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tinkerpop.gremlin.java.GremlinPipeline#outV()
	 */
	@Override
	public FramedTraversal<S, Vertex> outV() {
		return (FramedTraversal<S, Vertex>) super.outV();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tinkerpop.gremlin.java.GremlinPipeline#bothV()
	 */
	@Override
	public FramedTraversal<S, Vertex> bothV() {
		return (FramedTraversal<S, Vertex>) super.bothV();
	}

	/**
	 * Get the next object emitted from the pipeline. If no such object exists,
	 * then a NoSuchElementException is thrown.
	 * 
	 * @param kind
	 *            The type of frame for the element.
	 * @return the next emitted object
	 */
	public <T extends FramedElement<?>> T next(Class<T> kind) {

		return graph.frameElement((Element) super.next(), kind);
	}

	/**
	 * Return the next X objects in the traversal as a list.
	 * 
	 * @param number
	 *            the number of objects to return
	 * @param kind
	 *            the type of frame to for each element.
	 * @return a list of X objects (if X objects occur)
	 */
	public <T extends FramedElement<?>> List<T> next(int amount,
			final Class<T> kind) {
		return Lists.transform(super.next(amount), new Function<E, T>() {

			@Override
			public T apply(E e) {
				return graph.frameElement((Element) e, kind);
			}
		});

	}

	/**
	 * Return an iterator of framed elements.
	 * 
	 * @param kind
	 *            The kind of framed elements to return.
	 * @return An iterator of framed elements.
	 */
	@SuppressWarnings("unchecked")
	public <T extends FramedElement<?>> Iterator<T> frame(Class<T> kind) {
		return graph.frame((Iterator<Element>)this, kind);
	}

	/**
	 * Return a list of all the objects in the pipeline.
	 * 
	 * @param kind
	 *            The kind of framed elements to return.
	 * @return a list of all the objects
	 */
	public <T extends FramedElement<?>> List<T> toList(final Class<T> kind) {
		return Lists.transform(super.toList(), new Function<E, T>() {

			@Override
			public T apply(E e) {
				return graph.frameElement((Element) e, kind);
			}
		});

	}
=======
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tinkerpop.gremlin.java.GremlinPipeline#out(int,
	 * java.lang.String[])
	 */
	@Override
	public FramedTraversal<S, Vertex> out(final int branchFactor,
			final String... labels) {
		return (FramedTraversal<S, Vertex>) super.out(branchFactor, labels);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tinkerpop.gremlin.java.GremlinPipeline#out(java.lang.String[])
	 */
	@Override
	public FramedTraversal<S, Vertex> out(final String... labels) {
		return (FramedTraversal<S, Vertex>) super.out(labels);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tinkerpop.gremlin.java.GremlinPipeline#in(int,
	 * java.lang.String[])
	 */
	@Override
	public FramedTraversal<S, Vertex> in(final int branchFactor,
			final String... labels) {
		return (FramedTraversal<S, Vertex>) super.in(branchFactor, labels);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tinkerpop.gremlin.java.GremlinPipeline#in(java.lang.String[])
	 */
	@Override
	public FramedTraversal<S, Vertex> in(final String... labels) {
		return (FramedTraversal<S, Vertex>) super.in(labels);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tinkerpop.gremlin.java.GremlinPipeline#both(int,
	 * java.lang.String[])
	 */
	@Override
	public FramedTraversal<S, Vertex> both(final int branchFactor,
			final String... labels) {
		return (FramedTraversal<S, Vertex>) super.both(branchFactor, labels);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tinkerpop.gremlin.java.GremlinPipeline#both(java.lang.String[])
	 */
	@Override
	public FramedTraversal<S, Vertex> both(final String... labels) {
		return (FramedTraversal<S, Vertex>) super.both(labels);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tinkerpop.gremlin.java.GremlinPipeline#outE(int,
	 * java.lang.String[])
	 */
	@Override
	public FramedTraversal<S, Edge> outE(final int branchFactor,
			final String... labels) {
		return (FramedTraversal<S, Edge>) super.outE(branchFactor, labels);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tinkerpop.gremlin.java.GremlinPipeline#outE(java.lang.String[])
	 */
	@Override
	public FramedTraversal<S, Edge> outE(final String... labels) {
		return (FramedTraversal<S, Edge>) super.outE(labels);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tinkerpop.gremlin.java.GremlinPipeline#inE(int,
	 * java.lang.String[])
	 */
	@Override
	public FramedTraversal<S, Edge> inE(final int branchFactor,
			final String... labels) {
		return (FramedTraversal<S, Edge>) super.inE(branchFactor, labels);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tinkerpop.gremlin.java.GremlinPipeline#inE(java.lang.String[])
	 */
	@Override
	public FramedTraversal<S, Edge> inE(final String... labels) {
		return (FramedTraversal<S, Edge>) super.inE(labels);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tinkerpop.gremlin.java.GremlinPipeline#bothE(int,
	 * java.lang.String[])
	 */
	@Override
	public FramedTraversal<S, Edge> bothE(final int branchFactor,
			final String... labels) {
		return (FramedTraversal<S, Edge>) super.bothE(branchFactor, labels);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tinkerpop.gremlin.java.GremlinPipeline#bothE(java.lang.String[])
	 */
	@Override
	public FramedTraversal<S, Edge> bothE(final String... labels) {
		return (FramedTraversal<S, Edge>) super.bothE(labels);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tinkerpop.gremlin.java.GremlinPipeline#inV()
	 */
	@Override
	public FramedTraversal<S, Vertex> inV() {
		return (FramedTraversal<S, Vertex>) super.inV();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tinkerpop.gremlin.java.GremlinPipeline#outV()
	 */
	@Override
	public FramedTraversal<S, Vertex> outV() {
		return (FramedTraversal<S, Vertex>) super.outV();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tinkerpop.gremlin.java.GremlinPipeline#bothV()
	 */
	@Override
	public FramedTraversal<S, Vertex> bothV() {
		return (FramedTraversal<S, Vertex>) super.bothV();
	}

    @Override
    public FramedTraversal filter(PipeFunction<E, Boolean> pipeFunction){
        return new FramedTraversal<>(graph, super.filter(pipeFunction).getStarts());
    }

    /**
	 * Get the next object emitted from the pipeline. If no such object exists,
	 * then a NoSuchElementException is thrown.
	 * 
	 * @param kind
	 *            The type of frame for the element.
	 * @return the next emitted object
	 */
	public <T extends FramedElement<?>> T next(Class<T> kind) {

		return graph.frameElement((Element) super.next(), kind);
	}

	/**
	 * Return the next X objects in the traversal as a list.
	 * 
	 * @param number
	 *            the number of objects to return
	 * @param kind
	 *            the type of frame to for each element.
	 * @return a list of X objects (if X objects occur)
	 */
	public <T extends FramedElement<?>> List<T> next(int amount,
			final Class<T> kind) {
		return Lists.transform(super.next(amount), new Function<E, T>() {

			@Override
			public T apply(E e) {
				return graph.frameElement((Element) e, kind);
			}
		});

	}

	/**
	 * Return an iterator of framed elements.
	 * 
	 * @param kind
	 *            The kind of framed elements to return.
	 * @return An iterator of framed elements.
	 */
	@SuppressWarnings("unchecked")
	public <T extends FramedElement<?>> Iterator<T> frame(Class<T> kind) {
		return graph.frame((Iterator<Element>)this, kind);
	}

	/**
	 * Return a list of all the objects in the pipeline.
	 * 
	 * @param kind
	 *            The kind of framed elements to return.
	 * @return a list of all the objects
	 */
	public <T extends FramedElement<?>> List<T> toList(final Class<T> kind) {
		return Lists.transform(super.toList(), new Function<E, T>() {

			@Override
			public T apply(E e) {
				return graph.frameElement((Element) e, kind);
			}
		});

	}
>>>>>>> /usr/src/app/output/syncleus/ferma/b273f174b303afbba124b948e959de1f0b4ea1d4/totorom-tinkerpop2/src/main/java/org/jglue/totorom/FramedTraversal.java/right.java
}
