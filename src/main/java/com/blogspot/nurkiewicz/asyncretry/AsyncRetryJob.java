package com.blogspot.nurkiewicz.asyncretry;

import com.blogspot.nurkiewicz.asyncretry.function.RetryCallable;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

/**
 * @author Tomasz Nurkiewicz
 * @since 7/21/13, 6:37 PM
 */
public class AsyncRetryJob<V> extends RetryJob<V> {

	private final RetryCallable<ListenableFuture<V>> userTask;

	public AsyncRetryJob(RetryCallable<ListenableFuture<V>> userTask, AsyncRetryExecutor parent) {
		this(userTask, parent, new AsyncRetryContext(parent.getRetryPolicy()), SettableFuture.<V>create());
	}

	public AsyncRetryJob(RetryCallable<ListenableFuture<V>> userTask, AsyncRetryExecutor parent, AsyncRetryContext context, SettableFuture<V> future) {
		super(context, parent, future);
		this.userTask = userTask;
	}

	@Override
	public void run(final long startTime) {
		try {
<<<<<<< /usr/src/app/output/nurkiewicz/async-retry/27316bb87f8ad572d714cb0907a04ed67cc92d7f/src/main/java/com/blogspot/nurkiewicz/asyncretry/AsyncRetryJob.java/left.java
			Futures.addCallback(userTask.call(context), new FutureCallback<V>() {
				@Override
				public void onSuccess(V result) {
					complete(result, System.currentTimeMillis() - startTime);
				}

				@Override
				public void onFailure(Throwable throwable) {
					handleThrowable(throwable, System.currentTimeMillis() - startTime);
				}
			});
||||||| /usr/src/app/output/nurkiewicz/async-retry/27316bb87f8ad572d714cb0907a04ed67cc92d7f/src/main/java/com/blogspot/nurkiewicz/asyncretry/AsyncRetryJob.java/base.java
			userTask.call(context).
					exceptionally(throwable -> {
						throwable.printStackTrace();
						handleThrowable(throwable, System.currentTimeMillis() - startTime);
						return null;
					}).thenAccept(result ->
							complete(result, System.currentTimeMillis() - startTime)
					);
=======
			userTask.call(context).handle((result, throwable) -> {
				final long stopTime = System.currentTimeMillis() - startTime;
				if (throwable != null) {
					handleThrowable(throwable, stopTime);
				} else {
					complete(result, stopTime);
				}
				return null;
			});
>>>>>>> /usr/src/app/output/nurkiewicz/async-retry/27316bb87f8ad572d714cb0907a04ed67cc92d7f/src/main/java/com/blogspot/nurkiewicz/asyncretry/AsyncRetryJob.java/right.java
		} catch (Throwable t) {
			handleThrowable(t, System.currentTimeMillis() - startTime);
		}
	}

	@Override
	protected RetryJob<V> nextTask(AsyncRetryContext nextRetryContext) {
		return new AsyncRetryJob<>(userTask, parent, nextRetryContext, future);
	}


}
