package de.goldman.csv.mataoln.domain;

import de.goldman.csv.mataoln.domain.model.SiteEntity;
import de.goldman.csv.mataoln.domain.model.SiteEntityRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class SitesService {

    @Autowired
    private SiteEntityRepository siteEntityRepository;

    @Transactional
    public Page<SiteEntity> loadSites(PageRequest pageable) {
        return this.siteEntityRepository.findAll(pageable);
    }
}
