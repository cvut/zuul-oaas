/*
 * The MIT License
 *
 * Copyright 2013-2014 Czech Technical University in Prague.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package cz.cvut.zuul.oaas.api.services;

import cz.cvut.zuul.oaas.api.models.ResourceDTO;
import cz.cvut.zuul.oaas.api.exceptions.NoSuchResourceException;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

@Validated
public interface ResourcesService {

    /**
     * @param id resource's id
     * @return resource if exists
     * @throws NoSuchResourceException when no matching resource was found
     */
    ResourceDTO findResourceById(String id) throws NoSuchResourceException;
    
    /**
     * @param resource resource to be created
     * @return id of the created resource
     */
    String createResource(@Valid ResourceDTO resource);
    
    /**
     * @param resource resource's content to be updated with
     * @throws NoSuchResourceException when no matching resource was found
     */
    void updateResource(@Valid ResourceDTO resource) throws NoSuchResourceException;

    /**
     * @param id id of the resource to be deleted
     * @throws NoSuchResourceException when no matching resource was found
     */
    void deleteResourceById(String id) throws NoSuchResourceException;
    
    /**
     * @return all resources
     */
    List<ResourceDTO> getAllResources();

    /**
     * @return all public resources
     */
    List<ResourceDTO> getAllPublicResources();
}
