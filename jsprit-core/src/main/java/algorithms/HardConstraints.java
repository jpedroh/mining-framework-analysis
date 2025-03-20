package algorithms;

/**
 * collection of hard constrainters bot at activity and at route level.
 * 
 * <p>HardPickupAndDeliveryLoadConstraint requires LOAD_AT_DEPOT and LOAD (i.e. load at end) at route-level
 * 
 * <p>HardTimeWindowConstraint requires LATEST_OPERATION_START_TIME
 * 
 * <p>HardPickupAndDeliveryConstraint requires LOAD_AT_DEPOT and LOAD at route-level and FUTURE_PICKS and PAST_DELIVIERS on activity-level
 * 
 * <p>HardPickupAndDeliveryBackhaulConstraint requires LOAD_AT_DEPOT and LOAD at route-level and FUTURE_PICKS and PAST_DELIVIERS on activity-level
 * 
 * @author stefan
 *
 */
class HardConstraints {
}