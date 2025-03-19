package com.github.davidmoten.rx.operators;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.concurrent.atomic.AtomicLong;
import rx.Observable;
import rx.Observable.Operator;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.observables.StringObservable;
import rx.observers.Subscribers;
import rx.subjects.PublishSubject;

/**
 * Reacts to source events by emitting new lines written to a file since the
 * last source event.
 */
public class OperatorFileTailer implements Operator<byte[], Object> {
  private final File file;

  private final AtomicLong currentPosition = new AtomicLong();

  private final int maxBytesPerEmission;

  /**
     * Constructor.
     * 
     * @param file
     *            text file to tail
     * @param startPosition
     *            start tailing the file after this many bytes
     */
  public OperatorFileTailer(File file, long startPosition, int maxBytesPerEmission) {
    if (file == null) {
      throw new NullPointerException("file cannot be null");
    }
    this.file = file;
    this.currentPosition.set(startPosition);
    this.maxBytesPerEmission = maxBytesPerEmission;
  }

  /**
     * Constructor. Emits byte arrays of up to 8*1024 bytes.
     * 
     * @param file
     * @param startPosition
     */
  public OperatorFileTailer(File file, long startPosition) {
    this(file, startPosition, 8192);
  }

  @Override public Subscriber<? super Object> call(Subscriber<? super byte[]> child) {
    final PublishSubject<? super Object> subject = PublishSubject.create();
    Subscriber<? super Object> parent = Subscribers.from(subject);
    child.add(parent);
    subject.concatMap(reportNewLines(file, currentPosition, maxBytesPerEmission)).unsafeSubscribe(child);
    return parent;
  }

  private static Func1<Object, Observable<byte[]>> reportNewLines(final File file, final AtomicLong currentPosition, final int maxBytesPerEmission) {
    return new Func1<Object, Observable<byte[]>>() {
      @Override public Observable<byte[]> call(Object event) {
        if (event instanceof WatchEvent) {
          WatchEvent<?> w = (WatchEvent<?>) event;
          String kind = w.kind().name();
          if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE.name())) {
            currentPosition.set(0);
          }
        }
        long length = file.length();
        if (length > currentPosition.get()) {

<<<<<<< /usr/src/app/output/davidmoten/rxjava-file/cb7c3d9a00024b03b9d23c6d5ad47ba35b3e092e/src/main/java/com/github/davidmoten/rx/operators/OperatorFileTailer.java/left.java
          try {
            final FileInputStream fis = new FileInputStream(file);
            fis.skip(currentPosition.get());
            return Observable.using(new Func0<InputStream>() {
              @Override public InputStream call() {
                return fis;
              }
            }, new Func1<InputStream, Observable<byte[]>>() {
              @Override public Observable<byte[]> call(InputStream t1) {
                return StringObservable.from(fis, maxBytesPerEmission).doOnNext(new Action1<byte[]>() {
                  @Override public void call(byte[] bytes) {
                    currentPosition.addAndGet(bytes.length);
                  }
                });
              }
            }, new Action1<InputStream>() {
              @Override public void call(InputStream is) {
                try {
                  is.close();
                } catch (IOException e) {
                }
              }
            });
          } catch (IOException e) {
            return Observable.error(e);
          }
=======
          Func0<InputStream> resourceFactory = new Func0<InputStream>() {
            @Override public InputStream call() {
              try {
                final FileInputStream fis = new FileInputStream(file);
                fis.skip(currentPosition.get());
                return fis;
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            }
          };
>>>>>>> /usr/src/app/output/davidmoten/rxjava-file/cb7c3d9a00024b03b9d23c6d5ad47ba35b3e092e/src/main/java/com/github/davidmoten/rx/operators/OperatorFileTailer.java/right.java

          Func1<InputStream, Observable<byte[]>> observableFactory = createObservableFactory(currentPosition, maxBytesPerEmission);
          Action1<InputStream> disposeAction = new Action1<InputStream>() {
            @Override public void call(InputStream is) {
              try {
                is.close();
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            }
          };
          return Observable.using(resourceFactory, observableFactory, disposeAction);
        } else {
          return Observable.empty();
        }
      }
    };
  }

  private static Func1<InputStream, Observable<byte[]>> createObservableFactory(final AtomicLong currentPosition, final int maxBytesPerEmission) {
    return new Func1<InputStream, Observable<byte[]>>() {
      @Override public Observable<byte[]> call(InputStream is) {
        return StringObservable.from(is, maxBytesPerEmission).doOnNext(new Action1<byte[]>() {
          @Override public void call(byte[] bytes) {
            currentPosition.addAndGet(bytes.length);
          }
        });
      }
    };
  }
}