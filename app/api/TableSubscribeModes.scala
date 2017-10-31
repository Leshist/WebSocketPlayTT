package api


object TableSubscribeModes {
  sealed abstract class SubscriptionMode
  case object Subscribed extends SubscriptionMode
  case object UnSubscribed extends SubscriptionMode
}
