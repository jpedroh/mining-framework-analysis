package strat.mining.stratum.proxy.worker;

import java.util.Date;
import java.util.Set;
import strat.mining.stratum.proxy.exception.ChangeExtranonceNotSupportedException;
import strat.mining.stratum.proxy.exception.TooManyWorkersException;
import strat.mining.stratum.proxy.json.MiningNotifyNotification;
import strat.mining.stratum.proxy.json.MiningSetDifficultyNotification;
import strat.mining.stratum.proxy.json.MiningSubmitRequest;
import strat.mining.stratum.proxy.json.MiningSubmitResponse;
import strat.mining.stratum.proxy.model.Share;
import strat.mining.stratum.proxy.network.Connection;
import strat.mining.stratum.proxy.pool.Pool;
import strat.mining.stratum.proxy.utils.Timer.Task;
import strat.mining.stratum.proxy.utils.Timer;


public interface WorkerConnection extends Connection {
	/**
	 * Return the pool on which this connection is bound.
	 * 
	 * @return
	 */
	public abstract Pool getPool();

	/**
	 * Return true if the connection is connected
	 * 
	 * @return
	 */
	public abstract boolean isConnected();

	/**
	 * Reset the connection with the parameters of the new pool. May close the
	 * connection if setExtranonce is not supported.
	 * 
	 * @param newPool
	 * @throws TooManyWorkersException
	 * @throws ChangeExtranonceNotSupportedException
	 */
	public abstract void rebindToPool(Pool newPool) throws TooManyWorkersException, ChangeExtranonceNotSupportedException;

	/**
	 * Called when the pool change its extranonce. Send the extranonce change to
	 * the worker. Throw an exception if the extranonce change is not supported
	 * on the fly.
	 */
	public abstract void onPoolExtranonceChange() throws ChangeExtranonceNotSupportedException;

	/**
	 * Called when the pool difficulty has changed
	 *
	 * @return 
	 */
	public abstract void onPoolDifficultyChanged(MiningSetDifficultyNotification notification);

	/**
	 * Called when the pool has send a new notify notification.
	 *
	 * @param notification
	 * 		
	 */
	public abstract void onPoolNotify(MiningNotifyNotification notification);

	/**
	 * Update the shares lists with the given share to compute hashrate
	 * 
	 * @param share
	 * @param isAccepted
	 */
	public abstract void updateShareLists(Share share, boolean isAccepted);

	/**
	 * Called when the pool has answered to a submit request.
	 * 
	 * @param workerRequest
	 * @param poolResponse
	 */
	public abstract void onPoolSubmitResponse(MiningSubmitRequest workerRequest, MiningSubmitResponse poolResponse);

	/**
	 * Set the sampling period to compute the hashrate of the connection. he
	 * period is in seconds.
	 * 
	 * @param samplingHashesPeriod
	 */
	public abstract void setSamplingHashesPeriod(Integer samplingHashesPeriod);

	/**
	 * Return the number of rejected hashes per seconds of the connection.
	 *
	 * @return 
	 */
	public abstract double getRejectedHashrate();

	/**
	 * Return the of accepted hashes per seconds of the connection.
	 *
	 * @return 
	 */
	public abstract double getAcceptedHashrate();

	/**
	 * Return a read-only set of users that are authorized on this connection.
	 * 
	 * @return
	 */
	public abstract Set<String> getAuthorizedWorkers();

	/**
	 * Return the of activation of this connection
	 * 
	 * @return
	 */
	public abstract Date getActiveSince();
}