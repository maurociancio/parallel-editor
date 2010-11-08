package ar.noxit.paralleleditor.client.converter

import ar.noxit.paralleleditor.common.messages.Response
import ar.noxit.paralleleditor.client.CommandFromKernel

trait ResponseConverter {
    def convert(response: Response): CommandFromKernel
}
