/*
 * Copyright 2020 indra953@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.indra.slide.networking;

import com.indra.slide.common.ANRequest;
import com.indra.slide.common.ANResponse;
import com.indra.slide.common.Priority;
import com.indra.slide.common.ResponseType;
import com.indra.slide.core.Core;
import com.indra.slide.error.ANError;
import com.indra.slide.utils.SourceCloseUtil;
import com.indra.slide.utils.Utils;

import okhttp3.Response;

import static com.indra.slide.common.RequestType.*;

/**
 * Created by indra953@gmail.com on 2020-02-08.
 */

public class InternalRunnable implements Runnable {

    private final Priority priority;
    public final int sequence;
    public final ANRequest request;

    public InternalRunnable(ANRequest request) {
        this.request = request;
        this.sequence = request.getSequenceNumber();
        this.priority = request.getPriority();
    }

    @Override
    public void run() {
        request.setRunning(true);
        switch (request.getRequestType()) {
            case SIMPLE:
                executeSimpleRequest();
                break;
            case DOWNLOAD:
                executeDownloadRequest();
                break;
            /*case MULTIPART:
                executeUploadRequest();
                break;*/
        }
        request.setRunning(false);
    }

    private void executeSimpleRequest() {
        Response okHttpResponse = null;
        try {
            okHttpResponse = InternalNetworking.performSimpleRequest(request);

            if (okHttpResponse == null) {
                deliverError(request, Utils.getErrorForConnection(new ANError()));
                return;
            }

            if (request.getResponseAs() == ResponseType.OK_HTTP_RESPONSE) {
                request.deliverOkHttpResponse(okHttpResponse);
                return;
            }
            if (okHttpResponse.code() >= 400) {
                deliverError(request, Utils.getErrorForServerResponse(new ANError(okHttpResponse),
                        request, okHttpResponse.code()));
                return;
            }

            ANResponse response = request.parseResponse(okHttpResponse);
            if (!response.isSuccess()) {
                deliverError(request, response.getError());
                return;
            }
            response.setOkHttpResponse(okHttpResponse);
            request.deliverResponse(response);
        } catch (Exception e) {
            deliverError(request, Utils.getErrorForConnection(new ANError(e)));
        } finally {
            SourceCloseUtil.close(okHttpResponse, request);
        }
    }

    private void executeDownloadRequest() {
        Response okHttpResponse;
        try {
            okHttpResponse = InternalNetworking.performDownloadRequest(request);
            if (okHttpResponse == null) {
                deliverError(request, Utils.getErrorForConnection(new ANError()));
                return;
            }
            if (okHttpResponse.code() >= 400) {
                deliverError(request, Utils.getErrorForServerResponse(new ANError(okHttpResponse),
                        request, okHttpResponse.code()));
                return;
            }
            request.updateDownloadCompletion();
        } catch (Exception e) {
            deliverError(request, Utils.getErrorForConnection(new ANError(e)));
        }
    }

    /*private void executeUploadRequest() {
        Response okHttpResponse = null;
        try {
            okHttpResponse = InternalNetworking.performUploadRequest(request);

            if (okHttpResponse == null) {
                deliverError(request, Utils.getErrorForConnection(new ANError()));
                return;
            }

            if (request.getResponseAs() == ResponseType.OK_HTTP_RESPONSE) {
                request.deliverOkHttpResponse(okHttpResponse);
                return;
            }

            if (okHttpResponse.code() >= 400) {
                deliverError(request, Utils.getErrorForServerResponse(new ANError(okHttpResponse),
                        request, okHttpResponse.code()));
                return;
            }
            ANResponse response = request.parseResponse(okHttpResponse);
            if (!response.isSuccess()) {
                deliverError(request, response.getError());
                return;
            }
            response.setOkHttpResponse(okHttpResponse);
            request.deliverResponse(response);
        } catch (Exception e) {
            deliverError(request, Utils.getErrorForConnection(new ANError(e)));
        } finally {
            SourceCloseUtil.close(okHttpResponse, request);
        }
    }*/

    public Priority getPriority() {
        return priority;
    }

    private void deliverError(final ANRequest request, final ANError anError) {
        Core.getInstance().getExecutorSupplier().forMainThreadTasks().execute(new Runnable() {
            public void run() {
                request.deliverError(anError);
                request.finish();
            }
        });
    }
}
