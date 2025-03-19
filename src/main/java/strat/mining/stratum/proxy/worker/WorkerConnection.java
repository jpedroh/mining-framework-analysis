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

import strat.mining.stratum.proxy.utils.Timer;

import strat.mining.stratum.proxy.utils.Timer.Task;

public class WorkerConnection extends StratumConnection {

	private Task subscribeTimeoutTask;

	@Override
	public void startReading() {
		super.startReading();
		subscribeTimeoutTask = new Task() {
			public void run() {
				LOGGER.warn("No subscribe request received from {} in {} ms. Closing connection.", getConnectionName(), subscribeReceiveTimeout);
				// Close the connection if subscribe request is not received at
				// time.
				close();
			}
		};
		Timer.getInstance().schedule(subscribeTimeoutTask, subscribeReceiveTimeout);
	}

	@Override
	protected void onSubscribeRequest(MiningSubscribeRequest request) {
		// Once the subscribe request is received, cancel the timeout timer.
		if (subscribeTimeoutTask != null) {
			subscribeTimeoutTask.cancel();
		}

		JsonRpcError error = null;
		try {
			pool = manager.onSubscribeRequest(this, request);
		} catch (NoPoolAvailableException e) {
			LOGGER.error("No pool available for the connection {}. Sending error and close the connection.", getConnectionName());
			error = new JsonRpcError();
			error.setCode(JsonRpcError.ErrorCode.UNKNOWN.getCode());
			error.setMessage("No pool available on this proxy.");
		}

		if (error == null) {
			try {
				extranonce1Tail = pool.getFreeTail();
				extranonce2Size = pool.getWorkerExtranonce2Size();
			} catch (TooManyWorkersException e) {
				LOGGER.error("Too many connections on pool {} for the connection {}. Sending error and close the connection.", pool.getName(),
						getConnectionName(), e);
				error = new JsonRpcError();
				error.setCode(JsonRpcError.ErrorCode.UNKNOWN.getCode());
				error.setMessage("Too many connection on the pool.");
			}
		}

		// Send the subscribe response
		MiningSubscribeResponse response = new MiningSubscribeResponse();
		response.setId(request.getId());
		if (error != null) {
			response.setErrorRpc(error);
		} else {
			response.setExtranonce1(pool.getExtranonce1() + extranonce1Tail);
			response.setExtranonce2Size(extranonce2Size);
			response.setSubscriptionDetails(getSubscibtionDetails());
			isActiveSince = new Date();
		}

		sendResponse(response);

		// If the subscribe succeed, send the initial notifications (difficulty
		// and notify).
		if (error == null) {
			sendInitialNotifications();
		}
	}
}

public interface WorkerConnection extends Connection {

	/**
	 * Return the pool on which this connection is bound.
	 * 
	 * @return
	 */
	public Pool getPool();

	/**
	 * Return true if the connection is connected
	 * 
	 * @return
	 */
	public boolean isConnected();

	/**
	 * Reset the connection with the parameters of the new pool. May close the
	 * connection if setExtranonce is not supported.
	 * 
	 * @param newPool
	 * @throws TooManyWorkersException
	 * @throws ChangeExtranonceNotSupportedException
	 */
	public void rebindToPool(Pool newPool) throws TooManyWorkersException, ChangeExtranonceNotSupportedException;

	/**
	 * Called when the pool change its extranonce. Send the extranonce change to
	 * the worker. Throw an exception if the extranonce change is not supported
	 * on the fly.
	 */
	public void onPoolExtranonceChange() throws ChangeExtranonceNotSupportedException;

	/**
	 * Called when the pool difficulty has changed
	 * 
	 * @param notification
	 */
	public void onPoolDifficultyChanged(MiningSetDifficultyNotification notification);

	/**
	 * Called when the pool has send a new notify notification.
	 * 
	 * @param notification
	 */
	public void onPoolNotify(MiningNotifyNotification notification);

	/**
	 * Update the shares lists with the given share to compute hashrate
	 * 
	 * @param share
	 * @param isAccepted
	 */
	public void updateShareLists(Share share, boolean isAccepted);

	/**
	 * Called when the pool has answered to a submit request.
	 * 
	 * @param workerRequest
	 * @param poolResponse
	 */
	public void onPoolSubmitResponse(MiningSubmitRequest workerRequest, MiningSubmitResponse poolResponse);

	/**
	 * Set the sampling period to compute the hashrate of the connection. he
	 * period is in seconds.
	 * 
	 * @param samplingHashesPeriod
	 */
	public void setSamplingHashesPeriod(Integer samplingHashesPeriod);

	/**
	 * Return the number of rejected hashes per seconds of the connection.
	 * 
	 * @return
	 */
	public double getRejectedHashrate();

	/**
	 * Return the of accepted hashes per seconds of the connection.
	 * 
	 * @return
	 */
	public double getAcceptedHashrate();

	/**
	 * Return a read-only set of users that are authorized on this connection.
	 * 
	 * @return
	 */
	public Set<String> getAuthorizedWorkers();

	/**
	 * Return the of activation of this connection
	 * 
	 * @return
	 */
	public Date getActiveSince();
}
