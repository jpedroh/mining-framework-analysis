package ninja;
import ninja.params.ControllerMethodInvoker;
import com.google.inject.Provider;

/**
 * The end of the filter chain
 *
 * @author James Roper
 */
class FilterChainEnd implements FilterChain {
  private Provider<?> controllerProvider;

  private ControllerMethodInvoker controllerMethodInvoker;

  FilterChainEnd(Provider<?> controllerProvider, ControllerMethodInvoker controllerMethodInvoker) {
    this.controllerProvider = controllerProvider;
    this.controllerMethodInvoker = controllerMethodInvoker;
  }

  @Override public Result next(Context context) {

<<<<<<< Unknown file: This is a bug in JDime.
=======
    if (result != null) {
      return result.copy();
    }
>>>>>>> /usr/src/app/output/ninjaframework/ninja/deba68759f3a7ae06bf3a5380a280eaae75beee2/ninja-core/src/main/java/ninja/FilterChainEnd.java/right.java

    Result controllerResult = (Result) controllerMethodInvoker.invoke(controllerProvider.get(), context);
    if (controllerResult instanceof AsyncResult) {
      context.handleAsync();
      Result newResult = context.controllerReturned();
      if (newResult != null) {
        controllerResult = newResult;
      }
    }
    return controllerResult;
  }
}