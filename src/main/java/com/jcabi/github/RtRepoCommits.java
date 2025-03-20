/**
 * Copyright (c) 2012-2013, JCabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import java.io.IOException;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;

/**
 * Commits of a Github repository.
 * @author Alexander Sinyagin (sinyagin.alexander@gmail.com)
 * @version $Id$
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = "request")
final class RtRepoCommits implements RepoCommits {

    /**
     * RESTful request for the commits.
     */
    private final transient Request request;
    /**
     * RESTful request, an entry point to the Github API.
     */
    private final transient Request entry;
    /**
     * Github.
     */
    private final transient Github github;
    /**
     * Repository.
     */
    private final transient Repo repo;
    /**
     * Public ctor.
     * @param req Entry point of API
     * @param repo Repository
     */
    RtRepoCommits(final Request req, final Repo repo) {
<<<<<<< /usr/src/app/output/jcabi/jcabi-github/3af65704843d882e9e391f5b19f9e7f8f404a3e5/src/main/java/com/jcabi/github/RtRepoCommits.java/left.java
        this.entry = req;
||||||| /usr/src/app/output/jcabi/jcabi-github/3af65704843d882e9e391f5b19f9e7f8f404a3e5/src/main/java/com/jcabi/github/RtRepoCommits.java/base.java
=======
        this.entry = req;
        this.owner = repo;
>>>>>>> /usr/src/app/output/jcabi/jcabi-github/3af65704843d882e9e391f5b19f9e7f8f404a3e5/src/main/java/com/jcabi/github/RtRepoCommits.java/right.java
        this.request = req.uri()
            .path("/repos")
            .path(repo.coordinates().user())
            .path(repo.coordinates().repo())
            .path("/commits")
            .back();
        this.github = new RtGithub(this.request);
        this.repo = new RtRepo(this.github, this.request, repo);
    }
    @Override
    public Iterable<Commit> iterate() {
        return new RtPagination<Commit>(
            this.request,
            new RtPagination.Mapping<Commit>() {
                @Override
                public Commit map(final JsonObject object) {
                    return get(object.getString("sha"));
                }
            }
        );
    }
    @Override
    public Commit get(final String sha) {
        return new RtCommit(this.entry, this.repo, sha);
    }
    /**
     * RESTful API entry point.
     */
    /**
     * Parent repository.
     */
    private final transient Repo owner;

    @Override
    public String toString() {
        return this.request.uri().get().toString();
    }

    @Override
    public JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }
}
