package com.nz.rpc.interceptor;

import com.nz.rpc.interceptor.exception.AddInterceptorException;
import lombok.Data;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Data
public class InterceptorChain {

    private List<Entry> entries;
    private Set<String> names;


    public InterceptorChain() {
        entries = new LinkedList<>();
        names = new HashSet<>();
    }


    public void addFirst(String name,Interceptor interceptor) throws AddInterceptorException {

        synchronized (this){
            checkDuplicateName(name);
            Entry entry = new Entry(name,interceptor);
            insertInterceptor(0,entry);
        }
    }
    public void addLast(String name,Interceptor interceptor)throws AddInterceptorException {

        synchronized (this){
            checkDuplicateName(name);
            Entry entry = new Entry(name,interceptor);
            insertInterceptor(entries.size(),entry);


        }
    }
    public void addAfter(String baseName,String name,Interceptor interceptor)throws AddInterceptorException {

        synchronized (this){
            checkDuplicateName(name);
            checkExistName(baseName);
            Entry entry = new Entry(name,interceptor);

            int index = getIndexByName(baseName);

            insertInterceptor(index+1,entry);

        }
    }
    public void addBefore(String baseName, String name,Interceptor interceptor)throws AddInterceptorException {

        synchronized (this){
            checkDuplicateName(name);
            checkExistName(baseName);
            Entry entry = new Entry(name,interceptor);
            int index = getIndexByName(baseName);
            insertInterceptor(index,entry);
        }
    }

    private int getIndexByName(String name) throws AddInterceptorException {

        for(Entry entry:entries){
            if(entry.name.equals(name)){
                return entries.indexOf(entry);
            }
        }
        throw new AddInterceptorException("添加拦截器失败，拦截器["+name+"]未存在！");

    }

    private void checkDuplicateName(String name) throws AddInterceptorException {

        if(names.contains(name)){
            throw new AddInterceptorException("添加拦截器失败，拦截器["+name+"]已经存在！");
        }
    }
    private void checkExistName(String name) throws AddInterceptorException {

        if(!names.contains(name)){
            throw new AddInterceptorException("添加拦截器失败，拦截器["+name+"]未存在！");
        }
    }


    private void insertInterceptor(int index,Entry entry){

        names.add(entry.name);
        entries.add(index,entry);
    }


    @Data
    static class Entry{
        String name;
        Interceptor interceptor;

        public Entry(String name, Interceptor interceptor) {
            super();
            this.name = name;
            this.interceptor = interceptor;
        }
    }

    public List<Interceptor> getInterceptor(){

        List<Interceptor> interceptors  = new LinkedList<>();
        synchronized (this){

            entries.forEach((v)->{
                interceptors.add(v.interceptor);
            });

        }

        return interceptors;
    }

}
