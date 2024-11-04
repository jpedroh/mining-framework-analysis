package io.cloudslang.engine.node.services;
import com.google.common.collect.Multimap;
import io.cloudslang.engine.node.entities.WorkerKeepAliveInfo;
import io.cloudslang.engine.node.entities.WorkerNode;
import io.cloudslang.score.api.nodes.WorkerStatus;
import java.util.List;

public interface WorkerNodeService {
  String keepAlive(String uuid);

  WorkerKeepAliveInfo newKeepAlive(String uuid);

  void create(String uuid, String password, String hostName, String installDir);

  void updateWorkerToDeleted(String uuid);

  void updateWorkerToNotDeleted(String uuid);

  List<WorkerNode> readAllNotDeletedWorkers();

  String up(String uuid, String version, String versionId);

  String up(String uuid);

  WorkerNode readByUUID(String uuid);

  boolean isActive(String workerUuid);

  WorkerNode findByUuid(String uuid);

  List<WorkerNode> readAllWorkers();

  List<String> readNonRespondingWorkers();

  List<WorkerNode> readWorkersByActivation(boolean isActive);

  void activate(String uuid);

  void deactivate(String uuid);

  void updateEnvironmentParams(String uuid, String os, String jvm, String dotNetVersion);

  void updateStatus(String uuid, WorkerStatus status);

  void updateWorkerBusynessValue(String uuid, int workerBusynessValue);

  void updateStatusInSeparateTransaction(String uuid, WorkerStatus status);

  List<String> readAllWorkerGroups();

  List<String> readWorkerGroups(String uuid);

  void updateWorkerGroups(String uuid, String... groupNames);

  Multimap<String, String> readGroupWorkersMapActiveAndRunningAndVersion(String versionId);

  void addGroupToWorker(String workerUuid, String group);

  void removeGroupFromWorker(String workerUuid, String group);

  List<String> readWorkerGroups(List<String> groups);

  void updateBulkNumber(String workerUuid, String bulkNumber);

  void updateWRV(String workerUuid, String wrv);

  List<String> readAllWorkersUuids();

  void updateVersion(String workerUuid, String version, String versionId);

  void migratePassword(String uuid, String password);
}