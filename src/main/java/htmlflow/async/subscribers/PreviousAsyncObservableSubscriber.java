<<<<<<< /usr/src/app/output/fmcarvalho/htmlflow/a89a8fa0e3425befa5503896bca392ce1bac437e/src/main/java/htmlflow/async/subscribers/PreviousAsyncObservableSubscriber.java/left.java
package htmlflow.async.subscribers;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class PreviousAsyncObservableSubscriber<T> implements Observer<T> {
    
    private final Runnable onTermination;
    
    public PreviousAsyncObservableSubscriber(Runnable onTermination) {
        this.onTermination = onTermination;
    }
    
    @Override
    public void onSubscribe(@NonNull Disposable d) {
        // Not used because we are not subscribing to this particular Subscriber
        // We are just using this to subscribe to an Observable
    }
    
    @Override
    public void onNext(@NonNull T t) {
        // Not used because we are not subscribing to this particular Subscriber
        // We are just using this to subscribe to an Observable
    }
    
    @Override
    public void onError(@NonNull Throwable e) {
        // Not used because we are not subscribing to this particular Subscriber
        // We are just using this to subscribe to an Observable
    }
    
    @Override
    public void onComplete() {
        onTermination.run();
    }
}
||||||| /usr/src/app/output/fmcarvalho/htmlflow/a89a8fa0e3425befa5503896bca392ce1bac437e/src/main/java/htmlflow/async/subscribers/PreviousAsyncObservableSubscriber.java/base.java
package htmlflow.async.subscribers;

public class PreviousAsyncObservableSubscriber<T> extends AbstractObservableSubscriber<T> {
    
    private final Runnable onTermination;
    
    public PreviousAsyncObservableSubscriber(Runnable onTermination) {
        this.onTermination = onTermination;
    }
    
    @Override
    public void onComplete() {
        onTermination.run();
    }
}
=======
fatal: path 'src/main/java/htmlflow/async/subscribers/PreviousAsyncObservableSubscriber.java' does not exist in '91c5c4b27e1d315ebdd7690049050e7fe79addc3'
>>>>>>> /usr/src/app/output/fmcarvalho/htmlflow/a89a8fa0e3425befa5503896bca392ce1bac437e/src/main/java/htmlflow/async/subscribers/PreviousAsyncObservableSubscriber.java/right.java
