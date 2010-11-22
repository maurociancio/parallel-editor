package ar.noxit.paralleleditor.common

import actors.Actor
import actors.scheduler.ResizableThreadPoolScheduler

object BaseActor {
    val scheduler = new ResizableThreadPoolScheduler()
    val daemonScheduler = new ResizableThreadPoolScheduler(true)
}

trait BaseActor extends Actor {
    override def scheduler = BaseActor.scheduler
}
