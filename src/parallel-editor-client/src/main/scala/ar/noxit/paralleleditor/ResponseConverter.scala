package ar.noxit.paralleleditor

import common.messages.Response

trait ResponseConverter {
    def convert(response: Response): Any
}
