package org.ei.opensrp.core.template;


public interface DefaultOptionsProvider {
        SearchFilterOption searchFilterOption();
        ServiceModeOption serviceMode();
        FilterOption villageFilter();
        SortingOption sortOption();
        String nameInShortFormForTitle();
        SearchType searchType();
    }