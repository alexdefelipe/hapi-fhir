/*-
 * #%L
 * HAPI FHIR Storage api
 * %%
 * Copyright (C) 2014 - 2023 Smile CDR, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package ca.uhn.fhir.jpa.dao;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.interceptor.model.RequestPartitionId;
import ca.uhn.fhir.jpa.model.search.SearchBuilderLoadIncludesParameters;
import ca.uhn.fhir.jpa.model.search.SearchRuntimeDetails;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.api.server.storage.IResourcePersistentId;
import ca.uhn.fhir.rest.param.DateRangeParam;
import org.hl7.fhir.instance.model.api.IBaseResource;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ISearchBuilder<T extends IResourcePersistentId> {
	String SEARCH_BUILDER_BEAN_NAME = "SearchBuilder";

	IResultIterator createQuery(SearchParameterMap theParams, SearchRuntimeDetails theSearchRuntime, RequestDetails theRequest, @Nonnull RequestPartitionId theRequestPartitionId);

	Long createCountQuery(SearchParameterMap theParams, String theSearchUuid, RequestDetails theRequest, RequestPartitionId theRequestPartitionId);

	void setMaxResultsToFetch(Integer theMaxResultsToFetch);

	void loadResourcesByPid(Collection<T> thePids, Collection<T> theIncludedPids, List<IBaseResource> theResourceListToPopulate, boolean theForHistoryOperation, RequestDetails theDetails);

	/**
	 * Use the loadIncludes that takes a parameters object instead.
	 */
	@Deprecated
	Set<T> loadIncludes(FhirContext theContext, EntityManager theEntityManager, Collection<T> theMatches, Collection<Include> theRevIncludes, boolean theReverseMode,
														DateRangeParam theLastUpdated, String theSearchIdOrDescription, RequestDetails theRequest, Integer theMaxCount);

	default Set<T> loadIncludes(SearchBuilderLoadIncludesParameters<T> theParameters) {
		return this.loadIncludes(
			theParameters.getFhirContext(),
			theParameters.getEntityManager(),
			theParameters.getMatches(),
			theParameters.getIncludeFilters(),
			theParameters.isReverseMode(),
			theParameters.getLastUpdated(),
			theParameters.getSearchIdOrDescription(),
			theParameters.getRequestDetails(),
			theParameters.getMaxCount()
		);
	}

	/**
	 * How many results may be fetched at once
	 */
	void setFetchSize(int theFetchSize);

	void setPreviouslyAddedResourcePids(List<T> thePreviouslyAddedResourcePids);

}
