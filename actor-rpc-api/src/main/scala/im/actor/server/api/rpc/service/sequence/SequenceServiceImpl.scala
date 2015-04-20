package im.actor.server.api.rpc.service.sequence

import scala.concurrent.{ExecutionContext, Future}

import akka.actor.{ActorRef, ActorSystem}
import slick.dbio
import slick.dbio.Effect.Read
import slick.driver.PostgresDriver.api._

import im.actor.api.rpc._
import im.actor.api.rpc.groups.Group
import im.actor.api.rpc.misc.{ResponseSeq, ResponseVoid}
import im.actor.api.rpc.peers.{GroupOutPeer, UserOutPeer}
import im.actor.api.rpc.sequence.{DifferenceUpdate, ResponseGetDifference, SequenceService}
import im.actor.api.rpc.users.{ Phone, User }
import im.actor.server.api.util.{ UserUtils, GroupUtils }
import im.actor.server.models
import im.actor.server.presences.{ PresenceManager, PresenceManagerRegion }
import im.actor.server.push.{ SeqUpdatesManagerRegion, SeqUpdatesManager }
import im.actor.server.session.{ SessionRegion, SessionMessage }

class SequenceServiceImpl(seqUpdManagerRegion: SeqUpdatesManagerRegion,
                          presenceManagerRegion: PresenceManagerRegion,
                          sessionRegion: SessionRegion)
                         (implicit db: Database, actorSystem: ActorSystem) extends SequenceService {

  import SeqUpdatesManager._
  import GroupUtils._
  import UserUtils._

  override implicit val ec: ExecutionContext = actorSystem.dispatcher

  override def jhandleGetState(clientData: ClientData): Future[HandlerResult[ResponseSeq]] = {
    val authorizedAction = requireAuth(clientData).map { implicit client =>
      for {
        seqstate <- getSeqState(seqUpdManagerRegion, client.authId)
      } yield Ok(ResponseSeq(seqstate._1, seqstate._2))
    }

    db.run(toDBIOAction(authorizedAction))
  }


  override def jhandleGetDifference(seq: Int, state: Array[Byte], clientData: ClientData): Future[HandlerResult[ResponseGetDifference]] = {
    val authorizedAction = requireAuth(clientData).map { implicit client =>
      for {
        seqstate <- getSeqState(seqUpdManagerRegion, client.authId)
        (updates, needMore) <- getDifference(client.authId, state)
        (diffUpdates, userIds, groupIds) = extractDiff(updates)
        (users, phones, groups) <- getUsersPhonesGroups(userIds, groupIds)
      } yield {
        // TODO: get users, groups and group members

        Ok(ResponseGetDifference(
          seq = seq,
          state = state,
          updates = diffUpdates,
          needMore = needMore,
          users = users.toVector,
          groups = groups.toVector,
          phones = phones.toVector,
          emails = Vector.empty))
      }
    }

    db.run(toDBIOAction(authorizedAction))
  }

  override def jhandleSubscribeToOnline(users: Vector[UserOutPeer], clientData: ClientData): Future[HandlerResult[ResponseVoid]] = {
    val authorizedAction = requireAuth(clientData).map { client =>
      // FIXME: #security check access hashes
      val userIds = users.map(_.userId).toSet

      sessionRegion.ref ! SessionMessage.envelope(
        clientData.authId,
        clientData.sessionId,
        SessionMessage.SubscribeToOnline(userIds))

      DBIO.successful(Ok(ResponseVoid))
    }

    db.run(toDBIOAction(authorizedAction))
  }

  override def jhandleSubscribeFromOnline(users: Vector[UserOutPeer], clientData: ClientData): Future[HandlerResult[ResponseVoid]] = {
    val authorizedAction = requireAuth(clientData).map { client =>
      // FIXME: #security check access hashes
      val userIds = users.map(_.userId).toSet

      sessionRegion.ref ! SessionMessage.envelope(
        clientData.authId,
        clientData.sessionId,
        SessionMessage.SubscribeFromOnline(userIds))

      DBIO.successful(Ok(ResponseVoid))
    }

    db.run(toDBIOAction(authorizedAction))
  }

  override def jhandleSubscribeFromGroupOnline(groups: Vector[GroupOutPeer], clientData: ClientData): Future[HandlerResult[ResponseVoid]] = {
    // FIXME: #security check access hashes
    sessionRegion.ref ! SessionMessage.envelope(clientData.authId, clientData.sessionId, SessionMessage.SubscribeFromGroupOnline(groups.map(_.groupId).toSet))

    Future.successful(Ok(ResponseVoid))
  }

  override def jhandleSubscribeToGroupOnline(groups: Vector[GroupOutPeer], clientData: ClientData): Future[HandlerResult[ResponseVoid]] = {
    // FIXME: #security check access hashes
    sessionRegion.ref ! SessionMessage.envelope(clientData.authId, clientData.sessionId, SessionMessage.SubscribeToGroupOnline(groups.map(_.groupId).toSet))

    Future.successful(Ok(ResponseVoid))
  }

  private def extractDiff(updates: Seq[models.sequence.SeqUpdate]): (Vector[DifferenceUpdate], Set[Int], Set[Int]) = {
    updates.foldLeft[(Vector[DifferenceUpdate], Set[Int], Set[Int])](Vector.empty, Set.empty, Set.empty) {
      case ((updates, userIds, groupIds), update) =>
        (updates :+ DifferenceUpdate(update.header, update.serializedData),
          userIds ++ update.userIds,
          groupIds ++ update.groupIds)
    }
  }

  private def getUsersPhonesGroups(userIds: Set[Int], groupIds: Set[Int])
                                  (implicit client: AuthorizedClientData)
  : dbio.DBIOAction[(Seq[User], Seq[Phone], Seq[Group]), NoStream, Read with Read with Read with Read with Read with Read with Read with Read with Read] = {
    for {
      groups <- getGroupsStructs(groupIds)
      allUserIds = userIds ++ groups.foldLeft(Set.empty[Int]) { (ids, g) => ids ++ g.members.map(_.userId) }
      users <- userStructs(allUserIds)
      phones <- getUserPhones(allUserIds)
    } yield (users, phones, groups)
  }
}
