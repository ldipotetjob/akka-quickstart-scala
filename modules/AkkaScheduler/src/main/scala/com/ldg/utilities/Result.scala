package com.ldg.utilities

import com.ldg.moviepages.TitlesMoviePage
case class Result(failures: Option[FailureTrait], moviePage: Option[TitlesMoviePage])