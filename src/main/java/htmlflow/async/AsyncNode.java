<<<<<<< /usr/src/app/output/fmcarvalho/htmlflow/a89a8fa0e3425befa5503896bca392ce1bac437e/src/main/java/htmlflow/async/AsyncNode.java/left.java
package htmlflow.async;

import io.reactivex.rxjava3.core.Observable;
import org.xmlet.htmlapifaster.Element;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class AsyncNode<T> implements Cloneable {
    public final Runnable asyncAction;
    public AsyncNode next;
    public ChildNode childNode;
    public final Observable<T> observable;
    public CompletableFuture<Void> cf;
    
    public AsyncNode(AsyncNode next, ChildNode childNode, Runnable asyncAction, Observable<T> observable) {
        this.next = next;
        this.childNode = childNode;
        this.asyncAction = asyncAction;
        this.observable = observable;
    }
    
    public boolean isDone() {
        return cf != null && cf.isDone();
    }
    
    public boolean isRunning() {
        return cf != null;
    }
    
    public boolean isWaiting() {
        return cf == null;
    }
    
    @Override
    public AsyncNode<T> clone() {
        final AsyncNode<T> node = new AsyncNode<>(this.next, this.childNode, this.asyncAction, this.observable);
        node.cf = this.cf;
        return node;
    }
    
    public void setDone() {
        this.cf.complete(null);
    }
    
    public void setRunning() {
        this.cf = new CompletableFuture<>();
    }
    
    public static class ChildNode<E extends Element> {
        public Supplier<E> elem;
        public OnAsyncAction onAsyncAction;
        
        public ChildNode(Supplier<E> elem, OnAsyncAction onAsyncAction) {
            this.elem = elem;
            this.onAsyncAction = onAsyncAction;
        }
        
    }
    
    public interface OnAsyncAction {
        void trigger(AsyncNode node);
    }
    
}
||||||| /usr/src/app/output/fmcarvalho/htmlflow/a89a8fa0e3425befa5503896bca392ce1bac437e/src/main/java/htmlflow/async/AsyncNode.java/base.java
package htmlflow.async;

import io.reactivex.rxjava3.core.Observable;
import org.xmlet.htmlapifaster.Element;

import java.util.function.Supplier;

public class AsyncNode<T> implements Cloneable{
    public Runnable asyncAction;
    public AsyncNode next;
    public ChildNode childNode;
    public Observable<T> observable;
    public volatile State state = State.WAITING;
    
    public AsyncNode(AsyncNode next, ChildNode childNode, Runnable asyncAction, Observable<T> observable) {
        this.next = next;
        this.childNode = childNode;
        this.asyncAction = asyncAction;
        this.observable = observable;
    }
    
    public boolean isDone() {
        return this.state.isDone();
    }
    
    public boolean isRunning() {
        return this.state.isRunning();
    }
    
    public boolean isCancelled() {
        return this.state.isCancelled();
    }
    
    public boolean isWaiting() {
        return this.state.isWaiting();
    }
    
    @Override
    public AsyncNode<T> clone() {
        final AsyncNode<T> node = new AsyncNode<>(this.next, this.childNode, this.asyncAction, this.observable);
        node.state = this.state;
        return node;
    }
    
    public static class ChildNode<E extends Element> {
        public Supplier<E> elem;
        public OnAsyncAction onAsyncAction;
        
        public ChildNode(Supplier<E> elem, OnAsyncAction onAsyncAction) {
            this.elem = elem;
            this.onAsyncAction = onAsyncAction;
        }
        
    }
    
    public interface OnAsyncAction {
        void trigger(State state);
    }
    
    public enum State {
        WAITING,
        RUNNING,
        CANCELLED,
        DONE;
    
        public boolean isDone() {
            return this == DONE;
        }
    
        public boolean isRunning() {
            return this == RUNNING;
        }
    
        public boolean isCancelled() {
            return this == CANCELLED;
        }
        
        public boolean isWaiting() {
            return this == WAITING;
        }
    }
}
=======
fatal: path 'src/main/java/htmlflow/async/AsyncNode.java' does not exist in '91c5c4b27e1d315ebdd7690049050e7fe79addc3'
>>>>>>> /usr/src/app/output/fmcarvalho/htmlflow/a89a8fa0e3425befa5503896bca392ce1bac437e/src/main/java/htmlflow/async/AsyncNode.java/right.java
