package com.jivesoftware.api.entity;

import com.jivesoftware.api.AbstractJiveClient;

import java.util.*;

/**
 * Created by ed.venaglia on 2/27/14.
 */
public class Entities<TYPE> implements Iterable<TYPE> {

    private final AbstractJiveClient jiveClient;

    private Boolean filtered;
    private Integer itemsPerPage;
    private Links links;
    private List<TYPE> list;
    private Boolean sorted;
    private Integer startIndex;
    private String suggestedQuery;
    private Integer totalResults;
    private Integer unread;
    private Boolean updatedSince;

    public Entities(AbstractJiveClient jiveClient) {
        this.jiveClient = jiveClient;
    }

    public void setList(Object jsonArray) {
        // todo
    }

    public Boolean getFiltered() {
        return filtered;
    }

    public void setFiltered(Boolean filtered) {
        this.filtered = filtered;
    }

    public Integer getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(Integer itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public Boolean getSorted() {
        return sorted;
    }

    public void setSorted(Boolean sorted) {
        this.sorted = sorted;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public String getSuggestedQuery() {
        return suggestedQuery;
    }

    public void setSuggestedQuery(String suggestedQuery) {
        this.suggestedQuery = suggestedQuery;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public Integer getUnread() {
        return unread;
    }

    public void setUnread(Integer unread) {
        this.unread = unread;
    }

    public Boolean getUpdatedSince() {
        return updatedSince;
    }

    public void setUpdatedSince(Boolean updatedSince) {
        this.updatedSince = updatedSince;
    }

    public int getSize() {
        return list.size();
    }

    public TYPE get(int index) {
        return list.get(index);
    }

    @Override
    public Iterator<TYPE> iterator() {
        return Collections.unmodifiableList(list).iterator();
    }

    public boolean hasNext() {
        return links != null && links.next != null;
    }

    public boolean hasPrevious() {
        return links != null && links.previous != null;
    }

    public Entities<TYPE> next() throws NoSuchElementException {
        return null; // todo
    }

    public Entities<TYPE> previous() throws NoSuchElementException {
        return null; // todo
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public static class Links {

        String next;
        String previous;

        public String getNext() {
            return next;
        }

        public void setNext(String next) {
            this.next = next;
        }

        public String getPrevious() {
            return previous;
        }

        public void setPrevious(String previous) {
            this.previous = previous;
        }
    }
}
